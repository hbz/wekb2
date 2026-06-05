package wekb

import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonOutput
import org.altcha.altcha.Altcha
import wekb.system.AltchaClient

@Secured(['permitAll'])
class AltchaController {

    public static final Altcha.Algorithm ALTCHA_ALGORITHM   = Altcha.Algorithm.SHA256
    public static final String ALTCHA_HMAC_KEY              = "HMAC_KEY"

    @Secured(['permitAll'])
    def prompt() {
        Map<String, Object> result = [:]
        result
    }

    @Secured(['permitAll'])
    def challenge() {
        Altcha.ChallengeOptions options = new Altcha.ChallengeOptions()
                .setAlgorithm(ALTCHA_ALGORITHM)
                .setHmacKey(ALTCHA_HMAC_KEY)
                .setSecureRandomNumber(true)
//                .setExpiresInSeconds(30)

        Altcha.Challenge challenge = Altcha.createChallenge(options)

        String json = JsonOutput.toJson([
            algorithm : challenge.algorithm,
            challenge : challenge.challenge,
            maxnumber : challenge.maxnumber,
            salt      : challenge.salt,
            signature : challenge.signature
        ])

        response.setContentType("application/json")
        render json
    }

    @Secured(['permitAll'])
    def submit() {
        String wt  = AltchaClient.getWidgetToken(request)
        String b64 = params.get(wt)

        boolean verified = Altcha.verifySolution(
                b64,
                ALTCHA_HMAC_KEY,
                true
        )
        if (verified) {
            AltchaClient.addNewClient(request)
        }

        redirect(url: params.origin)
    }
}
