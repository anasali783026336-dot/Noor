package com.example.data.ai

import com.example.data.model.VocabWord
import com.example.data.repository.QuranData
import com.example.data.repository.QuranEncyclopediaData
import com.example.data.repository.QuranVersesRepository

data class CitationItem(
    val title: String,
    val referenceSource: String,
    val surahNumber: Int? = null,
    val ayahNumber: Int? = null
)

data class QuizItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class TutorMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val citations: List<CitationItem> = emptyList(),
    val referencedAyah: Pair<Int, Int>? = null, // (surahNumber, ayahNumber)
    val referencedAyahText: String? = null,
    val quizItem: QuizItem? = null,
    val timestamp: Long = System.currentTimeMillis()
)

object AiQuranTutorEngine {

    fun processQuery(userQuery: String): TutorMessage {
        val query = userQuery.trim()
        val norm = QuranData.normalizeArabic(query)

        // 1. Check for Quiz / Educational Testing Intent
        if (norm.contains("اختبرني") || norm.contains("سؤال") || norm.contains("مسابقه") || norm.contains("اختبار")) {
            return generateEducationalQuiz(norm)
        }

        // 2. Check for Memorization / Planning Advice
        if (norm.contains("حفظ") || norm.contains("مراجعه") || norm.contains("جدول") || norm.contains("خطة") || norm.contains("طريقه الحفظ")) {
            return generateMemorizationAdvice(norm)
        }

        // 3. Check for Asbab Al-Nuzul (أسباب النزول)
        if (norm.contains("سبب نزول") || norm.contains("اسباب النزول") || norm.contains("لماذا نزلت")) {
            return handleAsbabNuzulQuery(norm)
        }

        // 4. Check for Vocabulary (معاني الكلمات)
        if (norm.contains("معنى") || norm.contains("ما معنى") || norm.contains("شرح كلمه") || norm.contains("مفرده")) {
            return handleVocabQuery(norm)
        }

        // 5. Check for Topical search (آيات عن...)
        if (norm.contains("ايات عن") || norm.contains("ايات في") || norm.contains("موضوع") || norm.contains("الصبر") || norm.contains("الرزق") || norm.contains("السكينه") || norm.contains("الوالدين")) {
            return handleTopicQuery(norm)
        }

        // 6. Check for Specific Surah / Ayah Tafsir
        val surahMatch = findSurahInQuery(norm)
        if (surahMatch != null) {
            val ayahNum = extractAyahNumber(norm) ?: 1
            val tafsir = QuranVersesRepository.getTafsirForAyah(surahMatch.number, ayahNum)
            val ayah = QuranVersesRepository.getVersesForSurah(surahMatch.number).firstOrNull { it.numberInSurah == ayahNum }

            val reply = buildString {
                append("تفسير وبيان سورة ${surahMatch.nameArabic} (الآية $ayahNum):\n\n")
                if (ayah != null) {
                    append("﴿${ayah.textUthmani}﴾\n\n")
                }
                append("• التفسير الميسر:\n${tafsir.tafsirMuyassar}\n\n")
                if (tafsir.vocabWords.isNotEmpty()) {
                    append("• معاني المفردات:\n")
                    tafsir.vocabWords.forEach { v ->
                        append("  - ${v.word}: ${v.meaning}\n")
                    }
                    append("\n")
                }
                if (tafsir.reflection.isNotBlank()) {
                    append("• وقفة تدبرية:\n${tafsir.reflection}\n\n")
                }
                append("⚠️ تنبيه: المسائل الفقهية والأحكام يرجع فيها إلى أهل العلم والفتوى المعتمدين.")
            }

            return TutorMessage(
                isUser = false,
                text = reply,
                citations = listOf(
                    CitationItem("سورة ${surahMatch.nameArabic} - الآية $ayahNum", "المصحف الشريف بالرسم العثماني", surahMatch.number, ayahNum),
                    CitationItem("التفسير الميسر", "مجمع الملك فهد لطباعة المصحف الشريف", surahMatch.number, ayahNum)
                ),
                referencedAyah = Pair(surahMatch.number, ayahNum),
                referencedAyahText = ayah?.textUthmani
            )
        }

        // 7. Fallback Search in Quran
        val searchHits = QuranVersesRepository.searchAyahs(query)
        if (searchHits.isNotEmpty()) {
            val (s, a) = searchHits[0]
            val tafsir = QuranVersesRepository.getTafsirForAyah(s.number, a.numberInSurah)
            val reply = buildString {
                append("بناءً على استرجاع النص القرآني المعتمد، إليك الآية الكريمة المتعلقة ببحثك:\n\n")
                append("﴿${a.textUthmani}﴾\n")
                append("[سورة ${s.nameArabic} - الآية ${a.numberInSurah}]\n\n")
                append("• المعنى والبيان:\n${tafsir.tafsirMuyassar}\n\n")
                append("هل تود معرفة تفسير إضافي أو الاستماع لتلاوتها؟")
            }
            return TutorMessage(
                isUser = false,
                text = reply,
                citations = listOf(
                    CitationItem("سورة ${s.nameArabic} - الآية ${a.numberInSurah}", "المصحف الشريف", s.number, a.numberInSurah)
                ),
                referencedAyah = Pair(s.number, a.numberInSurah),
                referencedAyahText = a.textUthmani
            )
        }

        // Default informative guidance
        return TutorMessage(
            isUser = false,
            text = "أهلاً بك في «معلم القرآن بالذكاء الاصطناعي». أنا مساعدك القرآني الموثوق، أعتمد على قاعدة بيانات موثقة ومحققة من نصوص القرآن الكريم والتفاسير المعتمدة.\n\nيمكنك سؤالي عن:\n١. تفسير آية محددة (مثال: ما تفسير آية الكرسي؟).\n٢. أسباب النزول (مثال: ما سبب نزول سورة الضحى؟).\n٣. معاني المفردات الغريبة (مثال: ما معنى الصمد؟).\n٤. استخراج آيات في موضوع معين (مثال: آيات عن الصبر والفرج).\n٥. إعداد اختبارات وأسئلة تدبرية لحفظك.\n\nتفضل بطرح سؤالك وسأسترجعه لك من المصادر المعتمدة مباشرة.",
            citations = listOf(
                CitationItem("منصة نور للقرآن الكريم", "محرك الاسترجاع القرآني الموثق")
            )
        )
    }

    private fun handleAsbabNuzulQuery(norm: String): TutorMessage {
        val asbabAdDuha = QuranEncyclopediaData.getAsbabNuzul(93, 1)
        val asbabAlKawthar = QuranEncyclopediaData.getAsbabNuzul(108, 1)
        val asbabAlIkhlas = QuranEncyclopediaData.getAsbabNuzul(112, 1)

        val (title, asbab, surahNum, ayahNum) = when {
            norm.contains("الضحى") -> Quadruple("سورة الضحى", asbabAdDuha, 93, 1)
            norm.contains("الكوثر") -> Quadruple("سورة الكوثر", asbabAlKawthar, 108, 1)
            norm.contains("الاخلاص") || norm.contains("قل هو الله") -> Quadruple("سورة الإخلاص", asbabAlIkhlas, 112, 1)
            else -> Quadruple("سورة الضحى", asbabAdDuha, 93, 1)
        }

        val text = buildString {
            append("سبب نزول $title من المصادر المعتمدة:\n\n")
            append("${asbab?.reason}\n\n")
            append("• المصدر والمستند:\n${asbab?.authenticSource}")
        }

        return TutorMessage(
            isUser = false,
            text = text,
            citations = listOf(
                CitationItem(title, asbab?.authenticSource ?: "كتب أسباب النزول المعتمدة", surahNum, ayahNum)
            ),
            referencedAyah = Pair(surahNum, ayahNum)
        )
    }

    private fun handleVocabQuery(norm: String): TutorMessage {
        val vocabMap = mapOf(
            "الصمد" to ("اللَّهُ الصَّمَدُ [الإخلاص: 2]" to "السيد الذي كمل في سؤدده وشرفه، والذي تصمد إليه الخلائق في حوائجها ورغائبها، الذي لا جوف له ولا يأكل ولا يشرب."),
            "غاسق" to ("وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ [الفلق: 3]" to "الغاسق: الليل إذا أظلم ودخل في كل شيء، ووقب: دخل ظلامه واشتد سواده."),
            "النفاثات" to ("وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ [الفلق: 4]" to "الساحرات اللاتي يعقدن العقد وينفثن فيها بريقهن لإيقاع الأذى والسحر."),
            "الكوثر" to ("إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ [الكوثر: 1]" to "الخير الكثير الدائم في الدنيا والآخرة، ومنه نهر الكوثر العظيم في الجنة الذي وعد الله به نبيه ﷺ."),
            "الابتر" to ("إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ [الكوثر: 3]" to "المقطوع أثره وذكره من كل خير، فلا يبقى له ذكر طيب ولا عمل صالح.")
        )

        for ((k, v) in vocabMap) {
            if (norm.contains(QuranData.normalizeArabic(k))) {
                return TutorMessage(
                    isUser = false,
                    text = "شرح مفردة «$k» في القرآن الكريم:\n\n• الآية الكريمة:\n${v.first}\n\n• المعنى المعتمد في كتب غريب القرآن والتفسير:\n${v.second}",
                    citations = listOf(
                        CitationItem("غريب القرآن ومعاني الآيات", "المفردات للراغب الأصفهاني وتفسير الطبري")
                    )
                )
            }
        }

        return TutorMessage(
            isUser = false,
            text = "يمكنك كتابة الكلمة وسياق الآية الكريمة، وسأقوم بشرح معناها الإفرادي وسياقها في كتب غريب القرآن والتفسير الميسر.",
            citations = listOf(CitationItem("معجم مفردات القرآن", "المصادر اللغوية المعتمدة"))
        )
    }

    private fun handleTopicQuery(norm: String): TutorMessage {
        val topic = QuranEncyclopediaData.topics.firstOrNull { t ->
            norm.contains(QuranData.normalizeArabic(t.titleArabic.take(5))) ||
                    norm.contains(t.id) ||
                    (norm.contains("صبر") && t.id == "patience_relief") ||
                    (norm.contains("سكينه") && t.id == "healing_tranquility") ||
                    (norm.contains("توحيد") && t.id == "tawheed") ||
                    (norm.contains("جنه") && t.id == "jannah_bliss") ||
                    (norm.contains("اخلاق") && t.id == "morals_righteousness")
        } ?: QuranEncyclopediaData.topics[1] // default patience/relief

        val reply = buildString {
            append("فهرس الموضوعات القرآنية: «${topic.titleArabic}»\n\n")
            append("${topic.description}\n\n")
            append("أبرز الآيات في هذا الباب:\n")
            topic.ayahsList.forEach { item ->
                append("• ${item.third}\n")
            }
            append("\nيمكنك النقر على أي آية لفتحها وقراءة تفسيرها الكامل.")
        }

        val firstAyah = topic.ayahsList.firstOrNull()
        return TutorMessage(
            isUser = false,
            text = reply,
            citations = topic.ayahsList.map {
                CitationItem(it.third, "فهرس موضوعات القرآن الكريم", it.first, it.second)
            },
            referencedAyah = if (firstAyah != null) Pair(firstAyah.first, firstAyah.second) else null
        )
    }

    private fun generateEducationalQuiz(norm: String): TutorMessage {
        val quiz = QuizItem(
            question = "في قوله تعالى: ﴿فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝٥ إِنَّ مَعَ الْعُسْرِ يُسْرًا ۝٦﴾، ما الحكمة البلاغية من تكرار الآية مع تعريف العسر وتنكير اليسر؟",
            options = listOf(
                "أن العسر واحد معرّف بأل واليسر اثنان منكران، فلن يغلب عسرٌ يسرين",
                "للتأكيد فقط دون دلالة بيانية زائدة",
                "لأن العسر الأول في الدنيا واليسر في الآخرة حصراً"
            ),
            correctIndex = 0,
            explanation = "قال ابن عباس رضي الله عنهما: 'لن يغلب عسر يسرين'. فالعسر ذُكر معرفاً بأل فهو عسر واحد، بينما اليسر ذُكر منكراً في الآيتين فصار يسرين، إشعاراً بسعة رحمة الله وتضاعف التيسير."
        )

        return TutorMessage(
            isUser = false,
            text = "إليك سؤالاً تعليمياً وتدبرياً في كتاب الله:\n\n${quiz.question}",
            citations = listOf(
                CitationItem("سورة الشرح - الآيتان 5 و 6", "تفسير ابن كثير والبلاغة القرآنية", 94, 5)
            ),
            referencedAyah = Pair(94, 5),
            quizItem = quiz
        )
    }

    private fun generateMemorizationAdvice(norm: String): TutorMessage {
        val reply = buildString {
            append("إرشادات وخطة مقترحة للحفظ والمراجعة المتقنة:\n\n")
            append("١. التكرار والتثبيت: اقرأ الوجه الجديد أو المقطع 15 مرة نظراً في المصحف برسم واحد (مصحف المدينة) قبل محاولة الحفظ غيباً.\n")
            append("٢. الربط بالسياق: فهم معاني الآيات وأسباب النزول يرسخ الحفظ بنسبة تفوق 70% مقارنة بالحفظ المجرد.\n")
            append("٣. قاعدة التكرار المتباعد (Spaced Repetition):\n")
            append("   - مراجعة اليوم الأول: بعد ساعتين من الحفظ.\n")
            append("   - مراجعة اليوم التالي: مع الحفظ الجديد.\n")
            append("   - مراجعة نهاية الأسبوع: مراجعة تراكمية كاملة للورد الأسبوعي.\n")
            append("٤. التسميع الذاتي: استخدم «وضع التسميع الذاتي» في قسم الحفظ بتطبيق نور لكشف الكلمات تدريجياً والتحقق من حفظك.\n\n")
            append("قال الإمام الشافعي: 'شكوت إلى وكيع سوء حفظي .. فأرشدني إلى ترك المعاصي'.")
        }

        return TutorMessage(
            isUser = false,
            text = reply,
            citations = listOf(
                CitationItem("منهجية الحفظ المتقن", "إرشادات أئمة القراءات وعلوم الضبط")
            )
        )
    }

    private fun findSurahInQuery(norm: String): com.example.data.model.Surah? {
        for (surah in QuranData.surahsList) {
            val sNorm = QuranData.normalizeArabic(surah.nameArabic)
            if (norm.contains("سوره $sNorm") || norm.contains("سورة $sNorm") || norm.contains(sNorm)) {
                return surah
            }
        }
        return null
    }

    private fun extractAyahNumber(text: String): Int? {
        val regex = Regex("""(?:ايه|اية|الآية|الآيه)\s*(\d+)""")
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
