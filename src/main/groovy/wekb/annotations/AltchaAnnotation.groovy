package wekb.annotations

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Documented
@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)

@interface AltchaAnnotation {

    /**
     * The controller/action is accessible via <i>matchAll().excludes( .. )</i>
     * @see wekb.AltchaInterceptor#AltchaInterceptor()
     */
    public static final String ACCESS_ALLOWED = 'ACCESS_ALLOWED'

    String comment() default ''
}