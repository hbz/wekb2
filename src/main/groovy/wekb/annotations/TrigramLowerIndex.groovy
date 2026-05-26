package wekb.annotations

import java.lang.annotation.*

@Documented
@Target([ElementType.FIELD])
@Retention(RetentionPolicy.RUNTIME)

@interface TrigramLowerIndex {
    String collation() default ''
}