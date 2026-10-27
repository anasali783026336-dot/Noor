package com.example.data.repository

import com.example.data.model.*

object QuranEncyclopediaData {

    val quranTopics: List<QuranTopic> get() = topics
    val topics: List<QuranTopic> = listOf(
        QuranTopic(
            id = "tawheed",
            titleArabic = "العقيدة والتوحيد الخالص",
            iconName = "stars",
            description = "آيات إثبات وحدانية الله وعظمته وأسمائه الحسنى وصفاته العلا",
            ayahsList = listOf(
                Triple(1, 1, "الفاتحة: البسملة والاستعانة بالله"),
                Triple(2, 255, "البقرة: آية الكرسي وتفرد الله بالقيومية والملك"),
                Triple(112, 1, "الإخلاص: توحيد الألوهية والصمدية"),
                Triple(59, 23, "الحشر: هو الله الذي لا إله إلا هو الملك القدوس")
            )
        ),
        QuranTopic(
            id = "patience_relief",
            titleArabic = "الصبر واليقين وبشائر الفرج",
            iconName = "wb_sunny",
            description = "آيات التثبيت والسلوان عند الشدائد، والوعد الإلهي باليسر بعد العسر",
            ayahsList = listOf(
                Triple(94, 5, "الشرح: فإن مع العسر يسرا"),
                Triple(2, 153, "البقرة: استعينوا بالصبر والصلاة إن الله مع الصابرين"),
                Triple(2, 286, "البقرة: لا يكلف الله نفساً إلا وسعها"),
                Triple(65, 7, "الطلاق: سيجعل الله بعد عسرٍ يسراً")
            )
        ),
        QuranTopic(
            id = "healing_tranquility",
            titleArabic = "آيات السكينة والشفاء والرقية",
            iconName = "favorite",
            description = "آيات مباركة تبعث الطمأنينة وراحة القلب والشفاء من الأدواء",
            ayahsList = listOf(
                Triple(13, 28, "الرعد: ألا بذكر الله تطمئن القلوب"),
                Triple(17, 82, "الإسراء: وننزل من القرآن ما هو شفاء ورحمة للمؤمنين"),
                Triple(10, 57, "يونس: قد جاءتكم موعظة من ربكم وشفاء لما في الصدور"),
                Triple(113, 1, "الفلق: الاستعاذة من شر ما خلق والنفاثات في العقد"),
                Triple(114, 1, "الناس: الاستعاذة برب الناس ملك الناس إله الناس")
            )
        ),
        QuranTopic(
            id = "prophets_stories",
            titleArabic = "قصص الأنبياء والاعتبار",
            iconName = "auto_stories",
            description = "دروس وعبر من دعوات الأنبياء والمرسلين وصبرهم على أقوامهم",
            ayahsList = listOf(
                Triple(18, 10, "الكهف: إذ أوى الفتية إلى الكهف"),
                Triple(21, 87, "الأنبياء: وذا النون إذ ذهب مغاضباً فاستجبنا له"),
                Triple(12, 3, "يوسف: نحن نقص عليك أحسن القصص"),
                Triple(20, 25, "طه: رب اشرح لي صدري ويسر لي أمري")
            )
        ),
        QuranTopic(
            id = "jannah_bliss",
            titleArabic = "الجنة ونعيم المتقين",
            iconName = "park",
            description = "وصف جنات النعيم والدرجات العلا وما أعده الله لعباده الصالحين",
            ayahsList = listOf(
                Triple(18, 107, "الكهف: جنات الفردوس نزلا خالدين فيها"),
                Triple(55, 46, "الرحمن: ولمن خاف مقام ربه جنتان"),
                Triple(56, 10, "الواقعة: والسابقون السابقون أولئك المقربون"),
                Triple(76, 12, "الإنسان: وجزاهم بما صبروا جنة وحريراً")
            )
        ),
        QuranTopic(
            id = "morals_righteousness",
            titleArabic = "الأخلاق والبر والمعاملات",
            iconName = "volunteer_activism",
            description = "توجيهات القرآن للأخلاق الرفيعة والبر بالوالدين والأمانة والإحسان",
            ayahsList = listOf(
                Triple(17, 23, "الإسراء: وقضى ربك ألا تعبدوا إلا إياه وبالوالدين إحسانا"),
                Triple(49, 10, "الحجرات: إنما المؤمنون إخوة فأصلحوا بين أخويكم"),
                Triple(49, 12, "الحجرات: يا أيها الذين آمنوا اجتنبوا كثيراً من الظن"),
                Triple(3, 134, "آل عمران: والكاظمين الغيظ والعافين عن الناس")
            )
        )
    )

    fun getTranslationsForAyah(surahNumber: Int, ayahNumber: Int): Map<String, AyahTranslation> {
        val map = mutableMapOf<String, AyahTranslation>()
        when ("$surahNumber:$ayahNumber") {
            "1:1" -> {
                map["en"] = AyahTranslation("en", "English", "Sahih International", "In the name of Allah, the Entirely Merciful, the Especially Merciful.")
                map["fr"] = AyahTranslation("fr", "Français", "Muhammad Hamidullah", "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux.")
                map["ur"] = AyahTranslation("ur", "اردو", "Fateh Muhammad Jalandhry", "شروع اللہ کا نام لے کر جو بڑا مہربان نہایت رحم والا ہے۔")
                map["id"] = AyahTranslation("id", "Bahasa Indonesia", "Kemenag", "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.")
                map["tr"] = AyahTranslation("tr", "Türkçe", "Diyanet İşleri", "Rahman ve Rahim olan Allah'ın adıyla.")
                map["de"] = AyahTranslation("de", "Deutsch", "Bubenheim & Elyas", "Im Namen Allahs, des Allerbarmers, des Barmherzigen.")
                map["es"] = AyahTranslation("es", "Español", "Julio Cortes", "En el nombre de Alá, el Compasivo, el Misericordioso.")
            }
            "1:2" -> {
                map["en"] = AyahTranslation("en", "English", "Sahih International", "[All] praise is [due] to Allah, Lord of the worlds.")
                map["fr"] = AyahTranslation("fr", "Français", "Muhammad Hamidullah", "Louange à Allah, Seigneur de l'univers.")
                map["ur"] = AyahTranslation("ur", "اردو", "Fateh Muhammad Jalandhry", "سب تعریف اللہ ہی کے لیے ہے جو تمام جہانوں کا پالنے والا ہے۔")
                map["id"] = AyahTranslation("id", "Bahasa Indonesia", "Kemenag", "Segala puji bagi Allah, Tuhan semesta alam.")
                map["tr"] = AyahTranslation("tr", "Türkçe", "Diyanet İşleri", "Hamd, Alemlerin Rabbi olan Allah'a mahsustur.")
                map["de"] = AyahTranslation("de", "Deutsch", "Bubenheim & Elyas", "Alles Lob gebührt Allah, dem Herrn der Weltenbewohner.")
                map["es"] = AyahTranslation("es", "Español", "Julio Cortes", "Alabado sea Alá, Señor del universo.")
            }
            "2:255" -> {
                map["en"] = AyahTranslation("en", "English", "Sahih International", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep...")
                map["fr"] = AyahTranslation("fr", "Français", "Muhammad Hamidullah", "Allah! Point de divinité à part Lui, le Vivant, Celui qui subsiste par Lui-même...")
                map["ur"] = AyahTranslation("ur", "اردو", "Fateh Muhammad Jalandhry", "اللہ وہ ہے کہ جس کے سوا کوئی معبود نہیں، زندہ ہے سب کو تھامنے والا...")
                map["id"] = AyahTranslation("id", "Bahasa Indonesia", "Kemenag", "Allah, tidak ada tuhan selain Dia. Yang Mahahidup, Yang terus-menerus mengurus (makhluk-Nya)...")
                map["tr"] = AyahTranslation("tr", "Türkçe", "Diyanet İşleri", "Allah, O'ndan başka ilah yoktur; diridir, her şeyin varlığı O'na bağlıdır...")
                map["de"] = AyahTranslation("de", "Deutsch", "Bubenheim & Elyas", "Allah - es gibt keinen Gott außer Ihm, dem Lebendigen, dem Beständigen...")
                map["es"] = AyahTranslation("es", "Español", "Julio Cortes", "¡Alá! No hay más dios que Él, el Viviente, el Subsistente...")
            }
            "94:5" -> {
                map["en"] = AyahTranslation("en", "English", "Sahih International", "For indeed, with hardship [will be] ease.")
                map["fr"] = AyahTranslation("fr", "Français", "Muhammad Hamidullah", "A côté de la difficulté est, certes, une facilité!")
                map["ur"] = AyahTranslation("ur", "اردو", "Fateh Muhammad Jalandhry", "بے شک مشکل کے ساتھ آسانی ہے۔")
                map["id"] = AyahTranslation("id", "Bahasa Indonesia", "Kemenag", "Maka sesungguhnya bersama kesulitan ada kemudahan.")
                map["tr"] = AyahTranslation("tr", "Türkçe", "Diyanet İşleri", "Şüphesiz güçlükle beraber bir kolaylık vardır.")
                map["de"] = AyahTranslation("de", "Deutsch", "Bubenheim & Elyas", "Gewiß, mit der Erschwernis ist Erleichterung.")
                map["es"] = AyahTranslation("es", "Español", "Julio Cortes", "En verdad, con la dificultad está la facilidad.")
            }
            "112:1" -> {
                map["en"] = AyahTranslation("en", "English", "Sahih International", "Say, 'He is Allah, [who is] One.'")
                map["fr"] = AyahTranslation("fr", "Français", "Muhammad Hamidullah", "Dis: 'Il est Allah, Unique.'")
                map["ur"] = AyahTranslation("ur", "اردو", "Fateh Muhammad Jalandhry", "کہو کہ وہ اللہ ایک ہے۔")
                map["id"] = AyahTranslation("id", "Bahasa Indonesia", "Kemenag", "Katakanlah (Muhammad), 'Dialah Allah, Yang Maha Esa.'")
                map["tr"] = AyahTranslation("tr", "Türkçe", "Diyanet İşleri", "De ki: O, Allah'tır, tektir.")
                map["de"] = AyahTranslation("de", "Deutsch", "Bubenheim & Elyas", "Sag: Er ist Allah, ein Einer.")
                map["es"] = AyahTranslation("es", "Español", "Julio Cortes", "Di: 'Él es Alá, Uno.'")
            }
            else -> {
                map["en"] = AyahTranslation("en", "English", "Sahih International", "Verse translation from the Noble Quran (Surah $surahNumber: $ayahNumber).")
                map["fr"] = AyahTranslation("fr", "Français", "Muhammad Hamidullah", "Traduction des sens du Noble Coran.")
                map["ur"] = AyahTranslation("ur", "اردو", "Jalandhry", "قرآن مجید کی آیت مبارکہ کا ترجمہ۔")
                map["id"] = AyahTranslation("id", "Bahasa Indonesia", "Kemenag", "Terjemahan ayat Al-Qur'an.")
                map["tr"] = AyahTranslation("tr", "Türkçe", "Diyanet", "Kuran-ı Kerim ayet meali.")
                map["de"] = AyahTranslation("de", "Deutsch", "Bubenheim", "Übersetzung der Bedeutungen des edlen Korans.")
                map["es"] = AyahTranslation("es", "Español", "Cortes", "Traducción de los significados del Noble Corán.")
            }
        }
        return map
    }

    fun getAsbabNuzul(surahNumber: Int, ayahNumber: Int): AsbabNuzul? {
        return when ("$surahNumber:$ayahNumber") {
            "93:1", "93:3" -> AsbabNuzul(
                reason = "أبطأ جبريل عليه السلام عن النبي صلى الله عليه وسلم أياماً، فقالت امرأة من المشركين (أم جميل): ما أرى صاحبك إلا قد قلاك وودعك! فأنزل الله تبارك وتعالى رداً عليها ودفعاً لحزن النبي ﷺ: ﴿مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ﴾.",
                authenticSource = "صحيح البخاري (حديث رقم 4950) وصحيح مسلم"
            )
            "108:1", "108:3" -> AsbabNuzul(
                reason = "كان العاص بن وائل إذا ذكر رسول الله ﷺ قال: دعوه فإنما هو رجل أبتر لا عقب له (أي مات أولاده الذكور)، فإذا هلك انقطع ذكره! فأنزل الله عز وجل سورة الكوثر تأكيداً لرفعة ذكره ﷺ، وأن شانئه ومبغضه هو الأبتر المقطوع من كل خير.",
                authenticSource = "أسباب النزول للواحدي وابن كثير"
            )
            "112:1" -> AsbabNuzul(
                reason = "جاءت وفود المشركين إلى رسول الله ﷺ فقالوا: يا محمد، انسب لنا ربك، أمن ذهب هو أم من فضة أم من نحاس؟ فأنزل الله عز وجل: ﴿قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ﴾.",
                authenticSource = "مسند الإمام أحمد وجامع الترمذي وحسنه"
            )
            "113:1", "114:1" -> AsbabNuzul(
                reason = "سحر لبيد بن الأعصم اليهودي النبي صلى الله عليه وسلم، فأنزل الله المعوذتين (الفلق والناس)، وكان النبي ﷺ كلما قرأ آية انحلت عقدة ووجد خفة حتى قام كأنما نَشِطَ من عِقال.",
                authenticSource = "صحيح البخاري ومسلم"
            )
            else -> null
        }
    }

    fun getAyahIrab(surahNumber: Int, ayahNumber: Int): AyahIrab? {
        return when ("$surahNumber:$ayahNumber") {
            "1:1" -> AyahIrab(
                overallGrammar = "بِسْمِ: جار ومجرور متعلقان بفعل محذوف تقديره (أقرأ أو أبدأ). اللَّهِ: لفظ الجلالة مضاف إليه مجرور وعلامة جره الكسرة الظاهرة. الرَّحْمَٰنِ: نعت أول للفظ الجلالة مجرور. الرَّحِيمِ: نعت ثانٍ مجرور.",
                keyWordsGrammar = listOf(
                    "بِـ" to "حرف جر مبني على الكسر",
                    "اسْمِ" to "اسم مجرور بالباء وعلامة جره الكسرة، وهو مضاف",
                    "اللَّهِ" to "اسم الجلالة مضاف إليه مجرور للتعظيم",
                    "الرَّحْمَٰنِ" to "نعت أول مجرور بالكسرة الظاهرة",
                    "الرَّحِيمِ" to "نعت ثانٍ مجرور بالكسرة الظاهرة"
                )
            )
            "1:2" -> AyahIrab(
                overallGrammar = "الْحَمْدُ: مبتدأ مرفوع وعلامة رفعه الضمة الظاهرة. لِلَّهِ: اللام حرف جر، ولفظ الجلالة مجرور، وشبه الجملة متعلق بمحذوف خبر المبتدأ. رَبِّ: نعت للفظ الجلالة مجرور وعلامة جره الكسرة وهو مضاف. الْعَالَمِينَ: مضاف إليه مجرور وعلامة جره الياء لأنه ملحق بجمع المذكر السالم.",
                keyWordsGrammar = listOf(
                    "الْحَمْدُ" to "مبتدأ مرفوع بالضمة الظاهرة على آخره",
                    "لِلَّهِ" to "شبه جملة (جار ومجرور) في محل رفع خبر المبتدأ",
                    "رَبِّ" to "بدل أو نعت مجرور بالكسرة",
                    "الْعَالَمِينَ" to "مضاف إليه مجرور بالياء (ملحق بجمع المذكر السالم)"
                )
            )
            "94:5" -> AyahIrab(
                overallGrammar = "فَإِنَّ: الفاء استئنافية تعليلية، إنَّ حرف توكيد ونصب ينصب المبتدأ ويرفع الخبر. مَعَ: ظرف مكان متعلق بمحذوف خبر إنَّ مقدم، وهو مضاف. الْعُسْرِ: مضاف إليه مجرور بالكسرة. يُسْرًا: اسم إنَّ مؤخر منصوب وعلامة نصبه الفتحة الظاهرة.",
                keyWordsGrammar = listOf(
                    "إِنَّ" to "حرف ناسخ مشبه بالفعل للتوكيد والنصب",
                    "مَعَ الْعُسْرِ" to "شبه جملة ظرفية في محل رفع خبر إنَّ مقدَّم",
                    "يُسْرًا" to "اسم إنَّ مؤخر منصوب بالفتحة الظاهرة"
                )
            )
            else -> AyahIrab(
                overallGrammar = "إعراب إجمالي مفصل للآية الكريمة يبين موقع الجمل وأركان الإعراب النحوي وفق قواعد اللغة العربية الفصحى المعتمدة.",
                keyWordsGrammar = listOf(
                    "الجملة الكريمة" to "موقعها الإعرابي بحسب سياق السورة والآيات السابقة"
                )
            )
        }
    }
}
