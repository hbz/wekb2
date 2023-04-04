package wekb.utils

import org.apache.commons.lang3.RandomStringUtils

class PasswordUtils {

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

}
