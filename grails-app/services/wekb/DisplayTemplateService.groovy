package wekb

public class DisplayTemplateService {

  private globalDisplayTemplates = new java.util.HashMap<String,Map>()

  @javax.annotation.PostConstruct
  def init() {
    globalDisplayTemplates.put(CuratoryGroup.class.name,[rendername:'curatory_group' ])
    globalDisplayTemplates.put(Identifier.class.name,[rendername:'identifier', noCreate:true ])
    globalDisplayTemplates.put(IdentifierNamespace.class.name,[rendername:'identifier_namespace' ])
    globalDisplayTemplates.put(JobResult.class.name,[rendername:'job_result', noCreate:true ])
    globalDisplayTemplates.put(Org.class.name,[rendername:'org' ])
    globalDisplayTemplates.put(Package.class.name,[rendername:'package' ])
    globalDisplayTemplates.put(Platform.class.name,[rendername:'platform' ])
    globalDisplayTemplates.put(RefdataCategory.class.name,[rendername:'rdc' ])
    globalDisplayTemplates.put(RefdataValue.class.name,[rendername:'rdv'])
    globalDisplayTemplates.put(KbartSource.class.name,[rendername:'kbart_source' ])
    globalDisplayTemplates.put(TitleInstancePackagePlatform.class.name,[rendername:'tipp'])
    globalDisplayTemplates.put(UpdatePackageInfo.class.name,[rendername:'update_package_info', noCreate:true])
    globalDisplayTemplates.put(UpdateTippInfo.class.name,[rendername:'update_tipp_info', noCreate:true])
    globalDisplayTemplates.put(User.class.name,[rendername:'user'])
    globalDisplayTemplates.put(Vendor.class.name,[rendername:'vendor'])
    


  }

  public Map getTemplateInfo(String type) {
    return globalDisplayTemplates.get(type)
  }

}
