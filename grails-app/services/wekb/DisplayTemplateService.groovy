package wekb

public class DisplayTemplateService {

  private globalDisplayTemplates = new java.util.HashMap<String,Map>()

  @javax.annotation.PostConstruct
  def init() {
    globalDisplayTemplates.put('wekb.AdditionalPropertyDefinition',[ type:'staticgsp', rendername:'addpropdef' ])
    globalDisplayTemplates.put('wekb.CuratoryGroup',[ type:'staticgsp', rendername:'curatory_group' ])
    globalDisplayTemplates.put('wekb.Identifier',[ type:'staticgsp', rendername:'identifier', noCreate:true ])
    globalDisplayTemplates.put('wekb.IdentifierNamespace',[ type:'staticgsp', rendername:'identifier_namespace' ])
    globalDisplayTemplates.put('wekb.system.JobResult',[ type:'staticgsp', rendername:'job_result', noCreate:true ])
    globalDisplayTemplates.put('wekb.Org',[ type:'staticgsp', rendername:'org' ])
    globalDisplayTemplates.put('wekb.Package',[ type:'staticgsp', rendername:'package' ])
    globalDisplayTemplates.put('wekb.Platform',[ type:'staticgsp', rendername:'platform' ])
    globalDisplayTemplates.put('wekb.RefdataCategory',[ type:'staticgsp', rendername:'rdc' ])
    globalDisplayTemplates.put('wekb.RefdataValue',[ type:'staticgsp', rendername:'rdv', noCreate:true ])
    globalDisplayTemplates.put('wekb.Source',[ type:'staticgsp', rendername:'source' ])
    globalDisplayTemplates.put('wekb.TitleInstancePackagePlatform',[ type:'staticgsp', rendername:'tipp'])
    globalDisplayTemplates.put('wekb.UpdatePackageInfo',[ type:'staticgsp', rendername:'update_package_info', noCreate:true])
    globalDisplayTemplates.put('wekb.UpdateTippInfo',[ type:'staticgsp', rendername:'update_tipp_info', noCreate:true])
    globalDisplayTemplates.put('wekb.auth.User',[ type:'staticgsp', rendername:'user', noCreate:true ])
    


  }

  public Map getTemplateInfo(String type) {
    return globalDisplayTemplates.get(type)
  }

}
