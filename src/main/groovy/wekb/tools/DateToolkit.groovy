package wekb.tools

import wekb.normalizers.DateNormalizer
import groovy.time.TimeCategory
import org.apache.commons.lang.StringUtils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException

class DateToolkit {

    static List<String> DATE_TIME_FORMATS = ["yyyy-MM-dd", "dd-MM-yyyy", "M[M]/d[d]/yyyy", "d[d].M[M].yyyy",
                                             "yyyy", "yyyy-MM", "MM-yyyy", "yyyy-MM-dd'T'HH:mm:ss'Z'"]

    static String getDateMinusOneMinute(String date) {
        try {
            def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
            Date d = sdf.parse(date)
            use(TimeCategory) {
                d = d - 1.minute
                return sdf.format(d)
            }
        }
        catch (Exception e) {
            return date
        }
    }


    static LocalDateTime fromString(String dateString) throws IllegalArgumentException, DateTimeParseException{
        try{
            return fromString(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        }
        catch(IllegalArgumentException | DateTimeParseException e){
            return fromString(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
        }
    }


    static LocalDateTime fromString(String dateString, DateTimeFormatter formatter){
        return LocalDateTime.parse(dateString, formatter)
    }


    static String formatDate(LocalDateTime localDateTime){
        return formatDate(localDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }


    static String formatDate(LocalDateTime localDateTime, DateTimeFormatter formatter){
        return localDateTime.format(formatter)
    }


    static LocalDate getAsLocalDate(String value) {
        String dateString = DateNormalizer.getDateString(value)
        LocalDate itemLastUpdate = null
        if (!StringUtils.isEmpty(dateString)) {
            for (String dateTimeFormat in DATE_TIME_FORMATS){
                try{
                    DateTimeFormatter df = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(dateTimeFormat).toFormatter()
                    itemLastUpdate = LocalDate.parse(value, df)
                }
                catch(DateTimeParseException dtpe){ /* log only on method exit */ }
                if (itemLastUpdate != null){
                    return itemLastUpdate
                }
            }
        }
        //log.info("Could not parse date string: ${dateString}")
        return null
    }

}
