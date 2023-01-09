package gokb

import de.wekb.helper.RCConstants
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.gokb.cred.*
import org.springframework.security.access.annotation.Secured
import wekb.*

import java.lang.Package
import java.util.concurrent.ExecutorService

@Secured(['IS_AUTHENTICATED_FULLY'])
class WorkflowController{

  def genericOIDService
  def springSecurityService
  def reviewRequestService
  def dateFormatService
  GlobalSearchTemplatesService globalSearchTemplatesService
  ExportService exportService
  AutoUpdatePackagesService autoUpdatePackagesService
  AccessService accessService
  KbartImportService kbartImportService
  ExecutorService executorService

  def actionConfig = [
      'deleteIdentifierNamespace'        : [actionType: 'process', method: 'deleteIdentifierNamespace'],

      'manualKbartImport'  : [actionType: 'redirectToView', view: 'kbartImport', controller: 'package'],

      'method::deleteSoft'     : [actionType: 'simple'],
      'method::retire'         : [actionType: 'simple'],
      'method::removeWithTipps' : [actionType: 'simple'],
      'method::currentWithTipps': [actionType: 'simple'],
      'method::setCurrent'      : [actionType: 'simple'],

      /*'method::setExpected'    : [actionType: 'simple'],*/
      /*'method::RRTransfer'     : [actionType: 'workflow', view: 'revReqTransfer'],
      'method::RRClose'        : [actionType: 'simple'],*/

      'packageUrlUpdate'       : [actionType: 'process', method: 'triggerSourceUpdate'],
      'packageUrlUpdateAllTitles':[actionType:'process', method: 'triggerSourceUpdateAllTitles'],

      //'platform::replacewith'  : [actionType: 'workflow', view: 'platformReplacement'],
      /*'tipp::retire'           : [actionType: 'workflow', view: 'tippRetire'],
      'tipp::move'             : [actionType: 'workflow', view: 'tippMove'],*/

      'setStatus::Retired'     : [actionType: 'simple'],
      'setStatus::Current'     : [actionType: 'simple'],
      'setStatus::Expected'    : [actionType: 'simple'],
      'setStatus::Deleted'     : [actionType: 'simple'],
      'setStatus::Removed'     : [actionType: 'simple'],
      /*'org::deprecateReplace'  : [actionType: 'workflow', view: 'deprecateOrg'],
      'org::deprecateDelete'   : [actionType: 'workflow', view: 'deprecateDeleteOrg'],*/
      /*'verifyTitleList'        : [actionType: 'process', method: 'verifyTitleList'],*/
  ]


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def action(){
    log.debug("WorkflowController::action(${params})")
    def result = [:]
    result.ref = request.getHeader('referer')

    def action_config = actionConfig[params.selectedBulkAction]

    if (action_config){
      result.objects_to_action = []

      if (params.batch_on == 'all'){
        log.debug("Requested batch_on all.. so evaluate the query and do the right thing...")
        if (params.qbe){
          def qresult = [:]
          if (params.qbe.startsWith('g:')){
            // Global template, look in config
            def global_qbe_template_shortcode = params.qbe.substring(2, params.qbe.length())
            // log.debug("Looking up global template ${global_qbe_template_shortcode}")
            qresult.qbetemplate = globalSearchTemplatesService.getGlobalSearchTemplate(global_qbe_template_shortcode)
            // log.debug("Using template: ${result.qbetemplate}")
          }

          // Looked up a template from somewhere, see if we can execute a search
          if (qresult.qbetemplate){
            log.debug("Execute query")
            // doQuery(result.qbetemplate, params, result)
            def target_class = grailsApplication.getArtefact("Domain", qresult.qbetemplate.baseclass)
            com.k_int.HQLBuilder.build(grailsApplication, qresult.qbetemplate, params, qresult, target_class, genericOIDService)

            qresult.recset.each{
              def oid_to_action = "${it.class.name}:${it.id}"
              result.objects_to_action.add(genericOIDService.resolveOID2(oid_to_action))
            }
          }
        }
      }else if(params.component){
        def component = genericOIDService.resolveOID2(params.component)
        if(component){
          result.objects_to_action.add(component)
        }
      }
      else{
        log.debug("Assuming standard selection of rows to action")
        params.each{ p ->
          if ((p.key.startsWith('bulk:')) && (p.value) && (p.value instanceof String)){
            def oid_to_action = p.key.substring(5)
            result.objects_to_action.add(genericOIDService.resolveOID2(oid_to_action))
          }
        }
      }

      switch (action_config.actionType){
        case 'simple':
          def method_config = params.selectedBulkAction.split(/\:\:/) as List
          switch (method_config[0]){
            case "method":
              def context = [user: request.user]
              // Everything after the first 2 "parts" are args for the method.
              def method_params = []
              method_params.add(context)
              if (method_config.size() > 2){
                method_params.addAll(method_config.subList(2, method_config.size()))
              }
              // We should just call the method on the targets.
              result.objects_to_action.each{ def target ->
                log.debug("Target: ${target} (${target.class.name})")
                log.debug("Attempting to fire method ${method_config[1]} (${method_params})")
                // Wrap in a transaction.
                KBComponent.withTransaction{ def trans_status ->
                  try{
                    // Just try and fire the method.
                    target.invokeMethod("${method_config[1]}", method_params ? method_params as Object[] : null)
                    // Save the object.
                    target.save(failOnError: true)
                  }
                  catch (Throwable t){
                    // Rollback and log error.
                    trans_status.setRollbackOnly()
                    t.printStackTrace()
                    log.error("${t}")
                  }
                }
                // target.save(flush: true, failOnError:true)
                log.debug("After transaction: ${target?.status}")
              }
              result.objects_to_action.each{
                log.debug("${it.status}")
              }
              break
            case "setStatus":
              log.debug("SetStatus: ${method_config[1]}")
              def status_to_set = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, method_config[1])
              // def ota_ids = result.objects_to_action.collect{ it.id }
              if (status_to_set){
                def res = KBComponent.executeUpdate("update KBComponent as kbc set kbc.status = :st, kbc.lastUpdated = :currentDate where kbc IN (:clist)", [st: status_to_set, clist: result.objects_to_action, currentDate: new Date()])
                log.debug("Updated status of ${res} components")
              }
              break
          }
          // Do stuff
          redirect(url: result.ref)
          break
        case 'workflow':
          render view: action_config.view, model: result
          break
        case 'redirectToView':
          if(result.objects_to_action.size() == 1){
            redirect(controller: action_config.controller, action: action_config.view, id: result.objects_to_action[0].id)
          }else {
            flash.error = "This action can only be performed for one component! Try again, but only with one component."
          }

          break
        case 'process':
          this."${action_config.method}"(result.objects_to_action)
          break
        default:
          flash.error = "Invalid action type information: ${action_config.actionType}".toString()
          break
      }
    }
    else{
      flash.error = "Unable to locate action config for ${params.selectedBulkAction}".toString()
      log.warn("Unable to locate action config for ${params.selectedBulkAction}")
      redirect(url: result.ref)
    }
  }


  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def processPackageReplacement(){
    def retired_status = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Retired')
    def result = [:]
    result['old'] = []
    result['new'] = ''
    result['count'] = 0

    params.each{ p ->
      log.debug("Testing ${p.key}")
      if ((p.key.startsWith('tt')) && (p.value) && (p.value instanceof String)){
        def tt = p.key.substring(3)
        log.debug("Platform to replace: \"${tt}\"")
        def old_platform = Platform.get(tt)
        def new_platform = genericOIDService.resolveOID2(params.newplatform)
        log.debug("old: ${old_platform} new: ${new_platform}")
        try{
          def updates_count = Combo.executeQuery("select count(combo) from Combo combo where combo.fromComponent = ?", [old_platform])
          Combo.executeUpdate("update Combo combo set combo.fromComponent = ? where combo.fromComponent = ?", [new_platform, old_platform])
          result['count'] += updates_count
          result['old'] += old_platform.name
          result['new'] = new_platform.name
          old_platform.status = retired_status
          old_platform.save(flush: true)
        }
        catch (Exception e){
          log.debug("Problem executing update")
        }
      }
    }
    render view: 'platformReplacementResult', model: [result: result]
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def processTippRetire(){
    log.debug("processTippRetire ${params}")
    def retired_status = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Retired')
    def result = [:]

    params.list('beforeTipps').each{ title_oid ->
      log.debug("process ${title_oid}")
      def tipp_obj = genericOIDService.resolveOID2(title_oid)
      tipp_obj.status = retired_status
      if (params.endDateSelect == 'select' && params.selectedDate){
        tipp_obj.accessEndDate = params.date('selectedDate', 'yyyy-MM-dd')
      }
      else if (params.endDateSelect == 'now'){
        tipp_obj.accessEndDate = new Date()
      }
      tipp_obj.save(flush: true, failOnError: true)
    }
    redirect(url: params.ref)
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def processTippMove(){
    log.debug("processTippMove ${params}")
    def deleted_status = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
    def user = springSecurityService.currentUser
    def new_package = params.newpackage ? genericOIDService.resolveOID2(params.newpackage) : null
    def new_platform = params.newplatform ? genericOIDService.resolveOID2(params.newplatform) : null
    def tipps_to_action = params.list('beforeTipps')
    def new_title = params.newtitle ? genericOIDService.resolveOID2(params.newtitle) : null

    params.list('beforeTipps').each{ tipp_oid ->
      log.debug("process ${tipp_oid}")

      def tipp_obj = genericOIDService.resolveOID2(tipp_oid)
      def coverage = []

      tipp_obj.coverageStatements.each{ cst ->
        coverage.add(['startVolume'  : cst.startVolume ?: "",
                      'startIssue'   : cst.startIssue ?: "",
                      'endVolume'    : cst.endVolume ?: "",
                      'endIssue'     : cst.endIssue ?: "",
                      'embargo'      : cst.embargo ?: "",
                      'coverageNote' : cst.coverageNote ?: "",
                      'startDate'    : cst.startDate ? dateFormatService.formatTimestampMs(cst.startDate) : "",
                      'endDate'      : cst.endDate ? dateFormatService.formatTimestampMs(cst.endDate) : "",
                      'coverageDepth': cst.coverageDepth?.value ?: ""
        ])
      }

      def new_tipp = kBartImportService.tippUpsertDTO([
          package : ['internalId': (new_package ? new_package.id : tipp_obj.pkg.id)],
          platform: ['internalId': (new_platform ? new_platform.id : tipp_obj.hostPlatform.id)],
          title   : ['internalId': (new_title ? new_title.id : tipp_obj.title.id)],
          coverage: coverage,
          url     : tipp_obj.url
      ], user).save(flush: true, failOnError: true)

      log.debug("Created new TIPP ${new_tipp}")
      tipp_obj.status = deleted_status
      tipp_obj.save(flush: true, failOnError: true)
    }

    redirect(url: params.ref)
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def processRRTransfer(){
    def result = [:]
    log.debug("processRRTransfer ${params}")
    def new_user_alloc = genericOIDService.resolveOID2(params.allocToUser)

    params.each{ p ->
      if ((p.key.startsWith('tt:')) && (p.value) && (p.value instanceof String)){
        def tt = p.key.substring(3)
        def ReviewRequest rr = ReviewRequest.get(tt)
        log.debug("Process ${tt} - ${rr}")
        rr.needsNotify = true
        rr.save(flush: true)
        def rra = new ReviewRequestAllocationLog(note: params.note, allocatedTo: new_user_alloc, rr: rr).save(flush: true)
      }
    }

    result.ref = params.from
    redirect(url: result.ref)
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def newRRLink(){
    def new_rr = null
    log.debug("newRRLink ${params}")
    User user = springSecurityService.currentUser
    def stdDesc = params.stdDesc ?: null

    if (params.id){
      def component = KBComponent.findByUuid(params.id)

      if (!component){
        component = KBComponent.get(params.long('id'))
      }

      List<CuratoryGroup> curatoryGroups = component.respondsTo('curatoryGroups')

      new_rr = reviewRequestService.raise(component, params.request, "Manual Request", RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_TYPE, 'User Request'), null, null, stdDesc, curatoryGroups)
    }

    redirect(url: request.getHeader('referer') + '#review')
  }

  private writeExportLine(Writer writer, Closure<String> sanitize, TitleInstancePackagePlatform tipp, def tippCoverageStatement){
    writer.write(
        sanitize(tipp.name) + '\t' +
            (tipp.hasProperty('dateFirstInPrint') ? sanitize(tipp.getIdentifierValue('pISBN')) : sanitize(tipp.getIdentifierValue('ISSN'))) + '\t' +
            (tipp.hasProperty('dateFirstInPrint') ? sanitize(tipp.getIdentifierValue('ISBN')) : sanitize(tipp.getIdentifierValue('eISSN'))) + '\t' +
            sanitize(tippCoverageStatement.startDate) + '\t' +
            sanitize(tippCoverageStatement.startVolume) + '\t' +
            sanitize(tippCoverageStatement.startIssue) + '\t' +
            sanitize(tippCoverageStatement.endDate) + '\t' +
            sanitize(tippCoverageStatement.endVolume) + '\t' +
            sanitize(tippCoverageStatement.endIssue) + '\t' +
            sanitize(tipp.url) + '\t' +
            (tipp.title.hasProperty('firstAuthor') ? sanitize(tipp.title.firstAuthor) : '') + '\t' +
            sanitize(tipp.title.getId()) + '\t' +
            sanitize(tippCoverageStatement.embargo) + '\t' +
            sanitize(tippCoverageStatement.coverageDepth) + '\t' +
            sanitize(tippCoverageStatement.coverageNote) + '\t' +
            sanitize(tipp.title.getCurrentPublisher()?.name) + '\t' +
            sanitize(tipp.title.getPrecedingTitleId()) + '\t' +
            (tipp.title.hasProperty('dateFirstInPrint') ? sanitize(tipp.title.dateFirstInPrint) : '') + '\t' +
            (tipp.title.hasProperty('dateFirstOnline') ? sanitize(tipp.title.dateFirstOnline) : '') + '\t' +
            (tipp.title.hasProperty('volumeNumber') ? sanitize(tipp.title.volumeNumber) : '') + '\t' +
            (tipp.title.hasProperty('editionStatement') ? sanitize(tipp.title.editionStatement) : '') + '\t' +
            (tipp.title.hasProperty('firstEditor') ? sanitize(tipp.title.firstEditor) : '') + '\t' +
            '\t' +  // parent_publication_title_id
            sanitize(tipp.title?.medium?.value) + '\t' +  // publication_type
            sanitize(tipp.accessType?.value) + '\t' +  // access_type
            sanitize(tipp.title.getIdentifierValue('ZDB')) +
            '\n')
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def addToRulebase(){
    def result = [:]


    result.ref = request.getHeader('referer')
    log.debug("${params.sourceName}")
    log.debug("${params.sourceId}")

    def source = Source.get(params.sourceId)

    log.debug("Process existing rulebase:: ${source.ruleset}")

    // See if the source rulebase has been initialised
    def parsed_rulebase = source.ruleset ? JSON.parse(source.ruleset) : null
    if (parsed_rulebase == null){
      parsed_rulebase = [rules: [:]]
    }

    def num_probs = params.int('prob_seq_count')

    for (int i = 0; i < num_probs; i++){
      log.debug("addToRulebase ${params.pr['prob_res_' + i]}")
      def resolution = params.pr['prob_res_' + i]

      // If the user has specified what happens in this case, then store the rule in the source for subsequent use
      if (resolution.ResolutionOption){
        log.debug("When ${resolution.probfingerprint} Then ${resolution.ResolutionOption}")
        def rule_resolution = [ruleResolution: "${resolution.ResolutionOption}"]
        parsed_rulebase.rules[resolution.probfingerprint] = rule_resolution
      }
    }

    source.ruleset = parsed_rulebase as JSON
    source.save(flush: true, failOnError: true)

    redirect(url: result.ref)
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deprecateOrg(){
    def result = [:]
    log.debug("Params: ${params}")
    log.debug("otd: ${params.orgsToDeprecate}")
    log.debug("neworg: ${params.neworg}")
    if (params.orgsToDeprecate && params.neworg){
      def orgs = params.list('orgsToDeprecate')
      def neworg = genericOIDService.resolveOID2(params.neworg)

      orgs.each{ org_id ->

        def old_org = Org.get(org_id)

        if (old_org && neworg && accessService.checkEditableObject(old_org, params)){
          log.debug("Got org to deprecate and neworg...  Process now")
          // Updating all combo.toComponent
          // Updating all combo.fromComponent
          def old_from_combos = Combo.executeQuery("from Combo where fromComponent = ?", [old_org])
          def old_to_combos = Combo.executeQuery("from Combo where toComponent = ?", [old_org])

          old_from_combos.each{ oc ->
            def existing_new = Combo.executeQuery("from Combo where type = ? and fromComponent = ? and toComponent = ?", [oc.type, neworg, oc.toComponent])

            if (existing_new?.size() == 0 && oc.toComponent != neworg){
              oc.fromComponent = neworg
              oc.save(flush: true)
            }
            else{
              log.debug("New Combo already exists, or would link item to itself.. deleting instead!")
              oc.status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_DELETED)
              oc.save(flush: true)
            }
          }
          old_to_combos.each{ oc ->
            def existing_new = Combo.executeQuery("from Combo where type = ? and toComponent = ? and fromComponent = ?", [oc.type, neworg, oc.fromComponent])

            if (existing_new?.size() == 0 && oc.fromComponent != neworg){
              oc.toComponent = neworg
              oc.save(flush: true)
            }
            else{
              log.debug("New Combo already exists, or would link item to itself.. deleting instead!")
              oc.status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_DELETED)
              oc.save(flush: true)
            }
          }
          flash.success = "Org Deprecation Completed".toString()
        }
        else{
          flash.errors = "Org Deprecation Failed!".toString()
        }
      }
      redirect(controller: 'resource', action: 'show', id: "${neworg.class.name}:${neworg.id}")
    }
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deprecateDeleteOrg(){
    log.debug("deprecateDeleteOrg ${params}")
    def result = [:]
    if (params.orgsToDeprecate){
      def orgs = params.list('orgsToDeprecate')

      orgs.each{ org_id ->
        def o = Org.get(org_id)
        if (o){
          o.deprecateDelete()
        }
      }
    }
    result
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  private def verifyTitleList(packages_to_verify){
    def user = springSecurityService.currentUser
    boolean userAdmin = user.isAdmin()
    packages_to_verify.each{ ptv ->
      def pkgObj = Package.get(ptv.id)
      if (accessService.checkEditableObject(pkgObj, userAdmin)){
        pkgObj.save(flush: true, failOnError: true)
      }
    }
    redirect(url: request.getHeader('referer'))
  }

  private def triggerSourceUpdateAllTitles(packages_to_update) {
    triggerSourceUpdate(packages_to_update, true)
  }

  private def triggerSourceUpdate(packages_to_update, boolean allTitles=false) {
    log.info("triggerSourceUpdate for Packages ${packages_to_update}..")
    def user = springSecurityService.currentUser
    def pars = [:]
    def denied = false

    boolean userAdmin = user.isAdmin()

    if (packages_to_update.size() > 1){
      flash.error = "Please select a single Package to update!"
    }
    else{
      packages_to_update.each{ ptv ->
        def pkgObj = Package.get(ptv.id)

        if (pkgObj && pkgObj.source?.url){

          if (accessService.checkEditableObject(pkgObj, userAdmin)) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet()
            Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()])
            boolean processRunning = false
            threadArray.each { Thread thread ->
              if (thread.name == 'triggerSourceUpdate' + pkgObj.id) {
                processRunning = true
              }
            }

            if(processRunning){
              flash.error = 'A package update is already in progress. Please wait this has finished.'
            }else {
              executorService.execute({
                Package aPackage = Package.get(pkgObj.id)
                Thread.currentThread().setName('triggerSourceUpdate_' + pkgObj.id)
                autoUpdatePackagesService.startAutoPackageUpdate(aPackage, !allTitles)
              })

              flash.success = "The package update for Package '${pkgObj.name}' was started. This runs in the background. When the update has gone through, you will see this on the Auto Update Info of the package tab."
            }

          }
          else{
            flash.error = "No permissions to update this Package!"
          }
        }
        else if (!pkgObj){
          flash.error = "Unable to reference provided Package!"
        }
        else{
          flash.error = "Please check the Package Source for validity!"
        }
      }
    }
    log.debug('triggerSourceUpdate() done - redirecting')
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY'])
  def deleteIdentifierNamespace(identifierNamespaces) {
    log.info("deleteIdentifierNamespace ${identifierNamespaces}..")
    identifierNamespaces.each { idn ->
      IdentifierNamespace identifierNamespace = IdentifierNamespace.get(idn.id)
      if(!Org.findByPackageNamespace(identifierNamespace) && !Platform.findByTitleNamespace(identifierNamespace) && !Source.findByTargetNamespace(identifierNamespace) && !Identifier.findByNamespace(identifierNamespace)){
        identifierNamespace.delete(flush: true)
      }else {
        flash.error = "Identifier Namespace is linked with identifier or org or source or platform! Please unlink first!"
      }


    }
    redirect(url: request.getHeader('referer'))
  }
}
