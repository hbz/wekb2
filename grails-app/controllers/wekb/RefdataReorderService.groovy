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
        RefdataValue.executeUpdate('update RefdataValue rdv set rdv.order = 0 where rdv.owner = :federation and rdv.value = :aconet', [federation: platformFederation, aconet: 'ACOnet Identity Federation'])
        RefdataValue.executeUpdate('update RefdataValue rdv set rdv.order = 10 where rdv.owner = :federation and rdv.value = :dfn', [federation: platformFederation, dfn: 'DFN-AAI'])
        RefdataValue.executeUpdate('update RefdataValue rdv set rdv.order = 20 where rdv.owner = :federation and rdv.value = :switch', [federation: platformFederation, switch: 'SWITCHaai'])
        RefdataValue.executeUpdate('update RefdataValue rdv set rdv.order = 30 where rdv.owner = :federation and rdv.value = :edugain', [federation: platformFederation, edugain: 'eduGAIN'])
        Set<String> specialFederations = ['ACOnet Identity Federation', 'DFN-AAI', 'SWITCHaai', 'eduGAIN']
        order = 40
        RefdataValue.findAllByValueNotInListAndOwner(specialFederations, platformFederation).eachWithIndex { RefdataValue federation, int i ->
            federation.order = i*10+order
            federation.save()
        }
    }
}
