package wekb.utils

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.password.PasswordEncoder
import wekb.helper.BeanStore

import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordUtils {

    public static final String USER_PASSWORD_INFO =
            "The password must be between 8 and 20 characters long." +
                    "It must contain at least one uppercase letter, one lowercase letter, one number and one special character from the list ( !\$%&@#^+*~=:; )." +
                    "Spaces are not allowed."

    public static final String USER_PASSWORD_REGEX =
            '^' +
                    '(?=.*[A-Z])' +             // an upper case alphabet that must occur at least once
                    '(?=.*[a-z])' +             // a lower case alphabet must occur at least once
                    '(?=.*[0-9])' +             // a digit must occur at least once
                    '(?=.*[!$%&@#^+*~=:;])' +   // a special character that must occur at least once: !$%&@#^+*~=:;
                    '(?=\\S+$)' +               // white spaces are not allowed
                    '.{8,20}' +                 // 8 - 20 characters
                    '$'

    static final Pattern USER_PASSWORD_PATTERN = Pattern.compile(USER_PASSWORD_REGEX)

    static char[] getUserPasswordCharacters() {
        List<String> chars = []

        chars.addAll('A'..'Z')
        chars.addAll('a'..'z')
        chars.addAll('0'..'9')
        chars.addAll('!$%&@#+*'.toList())

        chars as char[]
    }

    static String getRandomUserPassword() {
        String password = 'hello password'
        char[] range = getUserPasswordCharacters()

        password = RandomStringUtils.random(16, 0, range.size() - 1, false, false, range)

        password
    }

    static boolean isUserPasswordValid(final String password) {
        Matcher matcher = USER_PASSWORD_PATTERN.matcher(password)
        return matcher.matches()
    }

}
