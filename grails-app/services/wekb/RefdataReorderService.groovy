package wekb



import grails.gorm.transactions.Transactional
import wekb.helper.RCConstants

/**
 * This service handles reference data reordering upon system startup. Customised orderings may be defined in the method below
 */
@Transactional
class RefdataReorderService {

    /**
     * This bootstrapped method should capsule every reordering queries so that no manual database migration scripts needs to be executed
     */
    void reorderRefdata() {
        // UYNP: Yes, Partially, No and Unavailable
        int order = 10
        List UYNP = RefdataValue.executeQuery("select rdv from RefdataValue as rdv where rdv.owner.desc = :uynp", [uynp: RCConstants.UYNP])

        UYNP.eachWithIndex { RefdataValue ct, int i ->
            switch(ct.value) {
                case 'Yes': ct.order = 0
                    break
                case 'Partially': ct.order = 10
                    break
                case 'No': ct.order = 20
                    break
                case 'Unavailable': ct.order = 30
                    break
                default: ct.order = i*order+40
                    break
            }
            ct.save()
        }

        //Federations: ACOnet, DFN, SWITCH, eduGAIN, other, none, rest
        RefdataCategory platformFederation = RefdataCategory.findByDesc(RCConstants.PLATFORM_FEDERATION)
        order = 40
        int i = 0
        RefdataValue.findAllByOwner(platformFederation).each { RefdataValue federation ->
            switch(federation.value) {
                case 'ACOnet Identity Federation': federation.order = 0
                    break
                case 'DFN-AAI': federation.order = 10
                    break
                case 'SWITCHaai': federation.order = 20
                    break
                case 'eduGAIN': federation.order = 30
                    break
                default: federation.order = i*10+order
                    i++
                    break
            }
            federation.save()
        }
    }
}
