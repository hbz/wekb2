package org.gokb

import de.wekb.helper.RCConstants
import org.gokb.cred.*
import com.k_int.ClassUtils

class ReviewRequestService {

  def raise(KBComponent forComponent, String actionRequired, String cause = null, RefdataValue type = null, refineProject = null, additionalInfo = null, RefdataValue stdDesc = null, List<CuratoryGroup> groups = null) {
    // Create a request.
    ReviewRequest req = new ReviewRequest(
            status: RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Open'),
            descriptionOfCause: (cause),
            reviewRequest: (actionRequired),
            stdDesc: (stdDesc),
            additionalInfo: (additionalInfo),
            componentToReview: (forComponent),
            type: type
    ).save();

    if (req) {

      if (groups) {
        groups.each {CuratoryGroup curatoryGroup ->
          AllocatedReviewGroup.create(curatoryGroup, req, true)
        }

      }
      else if (KBComponent.has(forComponent, 'curatoryGroups')) {
        log.debug("Using Component groups for ${forComponent} -> ${forComponent.class?.name}..")
        CuratoryGroup.withSession {
          forComponent.curatoryGroups?.each { gr ->
            CuratoryGroup cg = CuratoryGroup.get(gr.id)
            log.debug("Allocating Package Group ${gr} to review ${req}")
            AllocatedReviewGroup.create(cg, req, true)
          }
        }
      }
      else if (forComponent.class == TitleInstancePackagePlatform && forComponent.pkg?.curatoryGroups?.size() > 0) {
        log.debug("Using TIPP pkg groups ..")
        forComponent.pkg?.curatoryGroups?.each { gr ->
          CuratoryGroup cg = CuratoryGroup.get(gr.id)
          log.debug("Allocating TIPP Pkg Group ${gr} to review ${req}")
          AllocatedReviewGroup.create(cg, req, true)
        }
      }
      /*else if (raisedBy?.curatoryGroups?.size() > 0) {
        log.debug("Using User groups ..")
        raisedBy.curatoryGroups.each { gr ->
          log.debug("Allocating User Group ${gr} to review ${req}")
          AllocatedReviewGroup.create(gr, req, true)
        }
      }*/
    }

    req
  }
}
