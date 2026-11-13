package wekb

import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonOutput
import org.altcha.altcha.Altcha
import org.grails.web.json.JSONObject
import wekb.annotations.AltchaAnnotation
import wekb.system.AltchaClient
import wekb.utils.ServerUtils

import java.nio.charset.StandardCharsets

@Secured(['permitAll'])
@AltchaAnnotation(comment = AltchaAnnotation.ACCESS_ALLOWED)
class AltchaController {

    public static final Altcha.Algorithm ALTCHA_ALGORITHM   = Altcha.Algorithm.SHA256
    public static final String ALTCHA_HMAC_KEY              = "HMAC_KEY"

    @Secured(['permitAll'])
    def prompt() {
        log.info 'ALTCHA.PROMPT -> ' + request.getRemoteAddr()

        Map<String, Object> result = [:]
        result
    }

    @Secured(['permitAll'])
    def challenge() {
        Altcha.ChallengeOptions options = new Altcha.ChallengeOptions()
                .setAlgorithm(ALTCHA_ALGORITHM)
                .setHmacKey(ALTCHA_HMAC_KEY)
                .setSecureRandomNumber(true)
                .setExpiresInSeconds(20)
                .addParam('client', request.getRemoteAddr())

        Altcha.Challenge challenge = Altcha.createChallenge(options)

        String json = JsonOutput.toJson([
            algorithm : challenge.algorithm,
            challenge : challenge.challenge,
            maxnumber : challenge.maxnumber,
            salt      : challenge.salt,
            signature : challenge.signature
        ])

        response.setContentType('application/json')
        render json
    }

    @Secured(['permitAll'])
    def submit() {
        log.info 'ALTCHA.SUBMIT'

        String atn = AltchaClient.getChallengeName(request)
        String b64 = params.get(atn)

        if (b64) {
            boolean validSolution = Altcha.verifySolution(
                    b64,
                    ALTCHA_HMAC_KEY,
                    true
            )
            if (validSolution) {
                String payload = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8)
                JSONObject json = new JSONObject(payload)
                String client = Altcha.extractParams(json.getString('salt')).get('client')

                boolean validClient = (client == request.getRemoteAddr())
                if (validClient) {
                    AltchaClient.addNewClient(request)
                } else {
                    log.info 'ALTCHA.SUBMIT > valid solution BUT invalid client'
                }
            }
        }

        redirect(url: params.origin)
    }

    @Secured(['permitAll'])
    def revoke() {
        log.info 'ALTCHA.REVOKE (DEBUG/TESTING)'

        if (ServerUtils.getCurrentServer() in [ServerUtils.SERVER_LOCAL, ServerUtils.SERVER_DEV]) {
            String ch = AltchaClient.getClientHash(request)
            AltchaClient ac = AltchaClient.findByClient(ch)

            if (ac) {
                log.debug('client requested revoke: ' + ch)
                AltchaClient.removeClient(ac)
            }
        }
        redirect(controller: 'public', action: 'index')
    }
}
