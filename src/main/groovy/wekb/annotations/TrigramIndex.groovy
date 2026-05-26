package wekb.annotations

import java.lang.annotation.*

@Documented
@Target([ElementType.FIELD])
@Retention(RetentionPolicy.RUNTIME)

@interface TrigramIndex {
    String collation() default ''
}