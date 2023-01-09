package wekb;

public class DisplayTemplateService {

  private globalDisplayTemplates = new java.util.HashMap<String,Map>()

  @javax.annotation.PostConstruct
  def init() {
    globalDisplayTemplates.put('org.gokb.cred.AdditionalPropertyDefinition',[ type:'staticgsp', rendername:'addpropdef' ]);
    globalDisplayTemplates.put('wekb.UpdatePackageInfo',[ type:'staticgsp', rendername:'update_package_info', noCreate:true]);
    globalDisplayTemplates.put('wekb.UpdateTippInfo',[ type:'staticgsp', rendername:'update_tipp_info', noCreate:true]);
    globalDisplayTemplates.put('org.gokb.cred.CuratoryGroup',[ type:'staticgsp', rendername:'curatory_group' ]);
    globalDisplayTemplates.put('org.gokb.cred.Identifier',[ type:'staticgsp', rendername:'identifier', noCreate:true ]);
    globalDisplayTemplates.put('org.gokb.cred.IdentifierNamespace',[ type:'staticgsp', rendername:'identifier_namespace' ]);
    globalDisplayTemplates.put('org.gokb.cred.JobResult',[ type:'staticgsp', rendername:'job_result', noCreate:true ]);
    globalDisplayTemplates.put('org.gokb.cred.Org',[ type:'staticgsp', rendername:'org' ]);
    globalDisplayTemplates.put('org.gokb.cred.Package',[ type:'staticgsp', rendername:'package' ]);
    globalDisplayTemplates.put('org.gokb.cred.Platform',[ type:'staticgsp', rendername:'platform' ]);
    globalDisplayTemplates.put('org.gokb.cred.RefdataCategory',[ type:'staticgsp', rendername:'rdc' ]);
    globalDisplayTemplates.put('org.gokb.cred.RefdataValue',[ type:'staticgsp', rendername:'rdv', noCreate:true ]);
    globalDisplayTemplates.put('org.gokb.cred.ReviewRequest',[ type:'staticgsp', rendername:'revreq' ]);
    globalDisplayTemplates.put('org.gokb.cred.Source',[ type:'staticgsp', rendername:'source' ]);
    globalDisplayTemplates.put('org.gokb.cred.TitleInstancePackagePlatform',[ type:'staticgsp', rendername:'tipp']);
    globalDisplayTemplates.put('org.gokb.cred.User',[ type:'staticgsp', rendername:'user', noCreate:true ]);
    globalDisplayTemplates.put('org.gokb.cred.UserOrganisation',[ type:'staticgsp', rendername:'user_org' ]);


  }

  public Map getTemplateInfo(String type) {
    return globalDisplayTemplates.get(type);
  }

}
