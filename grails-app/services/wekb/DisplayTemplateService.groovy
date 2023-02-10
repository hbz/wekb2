package wekb

public class DisplayTemplateService {

  private globalDisplayTemplates = new java.util.HashMap<String,Map>()

  @javax.annotation.PostConstruct
  def init() {
    globalDisplayTemplates.put('wekb.CuratoryGroup',[rendername:'curatory_group' ])
    globalDisplayTemplates.put('wekb.Identifier',[rendername:'identifier', noCreate:true ])
    globalDisplayTemplates.put('wekb.IdentifierNamespace',[rendername:'identifier_namespace' ])
    globalDisplayTemplates.put('wekb.system.JobResult',[rendername:'job_result', noCreate:true ])
    globalDisplayTemplates.put('wekb.Org',[rendername:'org' ])
    globalDisplayTemplates.put('wekb.Package',[rendername:'package' ])
    globalDisplayTemplates.put('wekb.Platform',[rendername:'platform' ])
    globalDisplayTemplates.put('wekb.RefdataCategory',[rendername:'rdc' ])
    globalDisplayTemplates.put('wekb.RefdataValue',[rendername:'rdv', noCreate:true ])
    globalDisplayTemplates.put('wekb.KbartSource',[rendername:'kbart_source' ])
    globalDisplayTemplates.put('wekb.TitleInstancePackagePlatform',[rendername:'tipp'])
    globalDisplayTemplates.put('wekb.UpdatePackageInfo',[rendername:'update_package_info', noCreate:true])
    globalDisplayTemplates.put('wekb.UpdateTippInfo',[rendername:'update_tipp_info', noCreate:true])
    globalDisplayTemplates.put('wekb.auth.User',[rendername:'user', noCreate:true ])
    


  }

  public Map getTemplateInfo(String type) {
    return globalDisplayTemplates.get(type)
  }

}
