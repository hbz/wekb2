package wekb.normalizers

import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern

@Slf4j
class DateNormalizer {

    static String START_DATE = "StartDate"
    static String END_DATE = "EndDate"
    static Pattern BRACKET_PATTERN = Pattern.compile("^\\[(.*)]-?\$")
    static Pattern DATE_SPAN_PATTERN = Pattern.compile(
            "^([\\d]{4}-[\\d]{4})|" +
                    "([\\d]{4}-[\\d]{2}-[\\d]{4}-[\\d]{2})|" +
                    "([\\d]{4}(-[\\d]{2}){2}-[\\d]{4}(-[\\d]{2}){2})|" +
                    "([\\d]{2}\\.[\\d]{4}-[\\d]{2}\\.[\\d]{4})|" +
                    "(([\\d]{2}\\.){2}[\\d]{4}-([\\d]{2}\\.){2}[\\d]{4})\$")
    static Pattern DATE_SPAN_GROUPER =
            Pattern.compile("(([\\d]{2}\\.){0,2}[\\d]{4}(-[\\d]{2}){0,2})-(([\\d]{2}\\.){0,2}[\\d]{4}(-[\\d]{2}){0,2})")

    static SimpleDateFormat YYYY = new SimpleDateFormat("yyyy")
    static SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM")
    static SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd")
    static SimpleDateFormat MM_YYYY = new SimpleDateFormat("MM.yyyy")
    static SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy")
    static SimpleDateFormat YYYY_MM_DD_HH_mm_SS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    static String YEAR = "^[0-9]{4}\$"
    static String YEAR_MM = "^[0-9]{4}-[0-9]{2}\$"
    static String YEAR_MM_DD = "^[0-9]{4}-[0-9]{2}-[0-9]{2}\$"
    static String MM_YEAR = "^[0-9]{2}.[0-9]{4}\$"
    static String DD_MM_YEAR = "^[0-9]{2}.[0-9]{2}.[0-9]{4}\$"
    static String YEAR_MM_DD_HH_mm_SS = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z\$"

    static SimpleDateFormat TARGET_FORMAT = YYYY_MM_DD
    static DateTimeFormatter TARGET_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")


    static String normalizeDate(String str, String dateType) {
        if (!str) {
            return str
        }
        str = removeBlanksAndBrackets(str)
        str = pickPartFromDateSpan(str, dateType)
        str = completeEndDate(str, dateType)
        Date date = formatDateTime(str)
        if (date != null){
            str = TARGET_FORMAT.format(date)
        }
        str
    }


    static String completeEndDate(String str, String dateType){
        if (dateType.equals(END_DATE)){
            if (str.matches("^[\\d]{4}\$")){
                return str.concat("-12-31")
            }
            if (str.matches("^[\\d]{4}-[\\d]{2}\$")){
                YearMonth yearMonth = new YearMonth(Integer.valueOf(str.substring(0,4)), Integer.valueOf(str.substring(5)))
                LocalDate endOfMonth = yearMonth.atEndOfMonth()
                return TARGET_FORMATTER.format(endOfMonth)
            }
        }
        str
    }


    static Date formatDateTime(String date){
        if (StringUtils.isEmpty(date)){
            return null
        }
        try {
            SimpleDateFormat format
            if (date.matches(YEAR)){
                format = YYYY
            }
            else if (date.matches(YEAR_MM)){
                format = YYYY_MM
            }
            else if (date.matches(YEAR_MM_DD)){
                format = YYYY_MM_DD
            }
            else if (date.matches(MM_YEAR)){
                format = MM_YYYY
            }
            else if (date.matches(DD_MM_YEAR)){
                format = DD_MM_YYYY
            }
            else if (date.matches(YEAR_MM_DD_HH_mm_SS)){
                format = YYYY_MM_DD_HH_mm_SS
            }
            if (format != null){
                return new Date(format.parse(date).getTime())
            }
        }
        catch (Exception exception) {
            log.error("Could not parse ".concat(date).concat(" as Date. Exception: ").concat(exception.getMessage()))
        }
        return null
    }


    private static String pickPartFromDateSpan(String str, String dateType){
        // Take only start part or end part of something like "01.01.2000-31.12.2000"
        if (str.matches(DATE_SPAN_PATTERN)){
            Matcher dateSpanGrouperMatcher = DATE_SPAN_GROUPER.matcher(str)
            dateSpanGrouperMatcher.matches()
            if (dateType.equals(START_DATE)){
                str = dateSpanGrouperMatcher.group(1)
            }
            else if (dateType.equals(END_DATE)){
                str = dateSpanGrouperMatcher.group(4)
            }
        }
        str
    }


    private static String removeBlanksAndBrackets(String str){
        str = str.replaceAll(" ", "")
        def bracketMatcher = BRACKET_PATTERN.matcher(str)
        if (bracketMatcher.find()){
            str = bracketMatcher.group(1)
        }
        str
    }


    static getDateString(String aString){
        if (StringUtils.isEmpty((aString))){
            return null
        }
        if (aString.contains("T")){
            return aString.substring(0, aString.indexOf("T"))
        }
        return aString
    }
}
