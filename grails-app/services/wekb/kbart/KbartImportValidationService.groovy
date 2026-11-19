package wekb.kbart

import grails.gorm.transactions.Transactional

@Transactional
class KbartImportValidationService {

    Map tippValidateForKbart(tippMap) {
        log.debug("Begin tippValidateForKbart")
        Map result = ['valid': true]
        String errorMessage = ""

        if (!tippMap.publication_title?.trim()) {
            result.valid = false
            errorMessage = "Missing publication title!"
        }

        if (result.valid && !tippMap.title_url) {
            result.valid = false
            errorMessage = "Missing title url!"

        }

        if (result.valid && (!tippMap.title_id)) {
            result.valid = false
            errorMessage = "Missing title_id!"

        }

        if (!result.valid) {
            log.warn("Tipp failed validation: ${errorMessage} -> ${tippMap}")
        }

        if (errorMessage) {
            result.errorMessage = errorMessage
        }

        log.debug("End tippValidateForKbart")

        return result
    }

}
