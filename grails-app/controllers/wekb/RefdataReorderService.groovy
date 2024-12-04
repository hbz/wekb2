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
     * !!! Be careful when using rdv.order.
     * This overwrites the sorting, so it may be sorted according to German values. Then the display is wrongly sorted in English!!!
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

    }
}
