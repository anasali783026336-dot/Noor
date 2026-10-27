package com.example.data.model

enum class AthkarCategory(val titleArabic: String, val iconName: String) {
    MORNING("أذكار الصباح", "wb_sunny"),
    EVENING("أذكار المساء", "nights_stay"),
    AFTER_PRAYER("أذكار بعد الصلاة", "mosque"),
    SLEEP("أذكار النوم", "bedtime"),
    QURANIC_DUA("أدعية قرآنية", "menu_book")
}

data class DhikrItem(
    val id: Int,
    val category: AthkarCategory,
    val textArabic: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val fadl: String,
    val source: String
)

object AthkarDataProvider {
    val items = listOf(
        // Morning Athkar
        DhikrItem(
            id = 1,
            category = AthkarCategory.MORNING,
            textArabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            targetCount = 1,
            fadl = "من قالها حين يصبح حُفظ في يومه",
            source = "صحيح مسلم"
        ),
        DhikrItem(
            id = 2,
            category = AthkarCategory.MORNING,
            textArabic = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ.",
            targetCount = 1,
            fadl = "سيد الاستغفار: من قالها موقنا بها فمات من يومه دخل الجنة",
            source = "صحيح البخاري"
        ),
        DhikrItem(
            id = 3,
            category = AthkarCategory.MORNING,
            textArabic = "بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
            targetCount = 3,
            fadl = "لم يضره شيء حتى يمسي",
            source = "سنن أبي داود والترمذي"
        ),
        DhikrItem(
            id = 4,
            category = AthkarCategory.MORNING,
            textArabic = "رَضِيتُ بِاللَّهِ رَبّاً، وَبِالإِسْلاَمِ دِيناً، وَبِمُحَمَّدٍ صلى الله عليه وسلم نَبِيّاً.",
            targetCount = 3,
            fadl = "كان حقاً على الله أن يرضيه يوم القيامة",
            source = "مسند أحمد والترمذي"
        ),
        DhikrItem(
            id = 5,
            category = AthkarCategory.MORNING,
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.",
            targetCount = 3,
            fadl = "تزن وتفضل أضعاف ما يسبح المسبحون",
            source = "صحيح مسلم"
        ),

        // Evening Athkar
        DhikrItem(
            id = 10,
            category = AthkarCategory.EVENING,
            textArabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            targetCount = 1,
            fadl = "حفظ وإقرار بتوحيد الله وفضله",
            source = "صحيح مسلم"
        ),
        DhikrItem(
            id = 11,
            category = AthkarCategory.EVENING,
            textArabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
            targetCount = 3,
            fadl = "لم يضره سمّ ولا شيء تلك الليلة",
            source = "صحيح مسلم"
        ),
        DhikrItem(
            id = 12,
            category = AthkarCategory.EVENING,
            textArabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي.",
            targetCount = 1,
            fadl = "دعاء العافية وحفظ الأركان الأربعة",
            source = "سنن أبي داود"
        ),

        // After Prayer
        DhikrItem(
            id = 20,
            category = AthkarCategory.AFTER_PRAYER,
            textArabic = "أَسْتَغْفِرُ اللَّهَ (ثلاثاً) .. اللَّهُمَّ أَنْتَ السَّلاَمُ، وَمِنْكَ السَّلاَمُ، تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَالإِكْرَامِ.",
            targetCount = 1,
            fadl = "سنة المصطفى ﷺ عقب كل صلاة مكتوبة",
            source = "صحيح مسلم"
        ),
        DhikrItem(
            id = 21,
            category = AthkarCategory.AFTER_PRAYER,
            textArabic = "سُبْحَانَ اللَّهِ (33) ، الْحَمْدُ لِلَّهِ (33) ، اللَّهُ أَكْبَرُ (33) ثم تمام المائة: لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ.",
            targetCount = 33,
            fadl = "غُفرت خطاياه وإن كانت مثل زبد البحر",
            source = "صحيح مسلم"
        ),
        DhikrItem(
            id = 22,
            category = AthkarCategory.AFTER_PRAYER,
            textArabic = "قراءة آية الكرسي: ﴿اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ...﴾",
            targetCount = 1,
            fadl = "من قرأها دبر كل صلاة مكتوبة لم يمنعه من دخول الجنة إلا أن يموت",
            source = "النسائي وصححه الألباني"
        ),

        // Sleep
        DhikrItem(
            id = 30,
            category = AthkarCategory.SLEEP,
            textArabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ.",
            targetCount = 1,
            fadl = "حفظ النفس عند النوم واليقظة",
            source = "متفق عليه"
        ),

        // Quranic Dua
        DhikrItem(
            id = 40,
            category = AthkarCategory.QURANIC_DUA,
            textArabic = "﴿رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ﴾",
            targetCount = 3,
            fadl = "أجمع وأكثر دعاء كان يدعو به النبي ﷺ",
            source = "سورة البقرة: 201"
        ),
        DhikrItem(
            id = 41,
            category = AthkarCategory.QURANIC_DUA,
            textArabic = "﴿رَبِّ اشْرَحْ لِي صَدْرِي ۝ وَيَسِّرْ لِي أَمْرِي ۝ وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي ۝ يَفْقَهُوا قَوْلِي﴾",
            targetCount = 1,
            fadl = "دعاء موسى عليه السلام لطلب التيسير وانشراح الصدر",
            source = "سورة طه: 25-28"
        ),
        DhikrItem(
            id = 42,
            category = AthkarCategory.QURANIC_DUA,
            textArabic = "﴿رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ﴾",
            targetCount = 1,
            fadl = "دعاء الراسخين في العلم للثبات على الهداية",
            source = "سورة آل عمران: 8"
        )
    )
}
