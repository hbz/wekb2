package wekb.base

abstract class AbstractI10n {

    public static final LOCALE_DE = Locale.GERMAN
    public static final LOCALE_EN = Locale.ENGLISH

    static supportedLocales = ['en', 'de']

    // get translation; current locale
    String getI10n(String property) {
        //getI10n(property, LocaleContextHolder.getLocale().toString())
        //SET DEFAULT ENGLISH
        getI10n(property, LOCALE_EN)
    }

    // get translation
    String getI10n(String property, Locale locale) {
        //getI10n(property, locale.toString())
        //SET DEFAULT ENGLISH
        getI10n(property, LOCALE_EN.toString())
    }
    // get translation
    String getI10n(String property, String locale) {
        String result
        locale = decodeLocale(locale)

        if (supportedLocales.contains(locale)) {
            result = this."${property}_${locale}"
        }
        else {
            result = "- requested locale ${locale} not supported -"
        }
        result = (result != 'null') ? result : ''
    }

    static String decodeLocale(String locale) {

        if(locale.contains("-")) {
            return locale.split("-").first().toLowerCase()
        }
        else if(locale.contains("_")) {
            return locale.split("_").first().toLowerCase()
        }
        else {
            return locale
        }
    }
    static String decodeLocale(Locale locale) {
        decodeLocale(locale.toString())
    }
}
