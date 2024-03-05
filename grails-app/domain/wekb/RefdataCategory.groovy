package wekb

import wekb.base.AbstractI10n
import groovy.util.logging.Slf4j
import org.apache.commons.logging.LogFactory

import javax.persistence.Transient

@Slf4j
class RefdataCategory extends AbstractI10n {

  public static rdv_cache = [:]
  private static rdc_cache = [:]

  static org.apache.commons.logging.Log static_logger = LogFactory.getLog(RefdataCategory)

  String desc
  String desc_en
  String desc_de
  String label
  Set values

  Date dateCreated
  Date lastUpdated

  // indicates this object is created via current bootstrap
  boolean isHardData = false

  static mapping = {
    id column: 'rdc_id'
    version column: 'rdc_version'
    label column: 'rdc_label'
    desc column: 'rdc_description', index: 'rdc_description_idx'
    values sort: 'value', order: 'asc'
    desc_en column: 'rdc_desc_en'
    desc_de column: 'rdc_desc_de'
    isHardData column: 'rdc_is_hard_data'

    dateCreated column: 'rdc_date_created'
    lastUpdated column: 'rdc_last_updated'

  }

  static hasMany = [
    values: RefdataValue
  ]

  static mappedBy = [
    values: 'owner'
  ]

  static constraints = {
    label(nullable: true, blank: true)
    dateCreated(nullable:true)
    lastUpdated(nullable:true)

    desc_de (nullable:true, blank:false)
    desc_en (nullable:true, blank:false)
  }

  String getOID() {
    "${this.class.name}:${id}"
  }


  static def lookup(category_name, value, def sortkey = null){
    if ((value == null) || (category_name == null)){
      throw new RuntimeException("Request to lookupOrCreate null value in category ${category_name}")
    }
    def result = null
    def rdv_cache_key = category_name + ':' + value + ':' + sortkey
    def rdv_id = rdv_cache[rdv_cache_key]
    if (rdv_id && rdv_id instanceof Long) {
      result = RefdataValue.get(rdv_id)
    }
    else if (!rdv_id instanceof Long) {
      throw new RuntimeException("Got a string value from rdv_cache for ${category_name}, ${value}!")
    }
    else {
      // The category.
      def cats = RefdataCategory.executeQuery('select c from RefdataCategory as c where lower(c.desc) = :desc', [desc: category_name.toLowerCase()]);
      def cat = null
      if (cats.size() == 0) {
        return result
      }
      else if (cats.size() == 1){
        cat = cats[0]
        result = RefdataValue.findByOwnerAndValueIlike(cat, value)
      }
      else {
        throw new RuntimeException("Multiple matching refdata category names")
      }
      if (result) {
        rdv_cache[rdv_cache_key] = result.id
      }
    }
    // return the refdata value.
    result
  }


  static RefdataValue[] lookup(category_name) {
    if (category_name == null)
      throw new RuntimeException("Request to lookup category ${category_name}");

    def result = null;

    def rdc_cache_key = category_name
    def rdc_id = rdc_cache[rdc_cache_key]
    if (rdc_id && rdc_id instanceof Long) {
      // cache hit
      result = RefdataValue.findAllByOwner(rdc_id);
    } else if (!rdc_id instanceof Long) {
      throw new RuntimeException("Got a wrong value from rdv_cache for ${category_name}!");
    } else {
      // The category.
      def cat = RefdataCategory.executeQuery('select c from RefdataCategory as c where lower(c.desc) = :desc', [desc: category_name.toLowerCase()]);

      if (cat.size() == 0) {
        return result
      } else {
        result = RefdataValue.findAllByOwner(cat)
      }

      if (result) {
        rdc_cache[rdc_cache_key] = result.id
      }
    }

    // return the refdata values.
    result
  }

  static RefdataValue lookupOrCreate(category_name, value) {
    return lookupOrCreate(category_name, value, null)
  }

  static RefdataValue lookupOrCreate(category_name, value, sortkey) {

    if ((value == null) || (category_name == null))
      throw new RuntimeException("Request to lookupOrCreate null value in category ${category_name}");

    def result = null;

    def rdv_cache_key = category_name + ':' + value + ':' + sortkey
    def rdv_id = rdv_cache[rdv_cache_key]
    if (rdv_id && rdv_id instanceof Long) {
      result = RefdataValue.get(rdv_id);
    } else if (!rdv_id instanceof Long) {
      throw new RuntimeException("Got a string value from rdv_cache for ${category_name}, ${value}!");
    } else {
      // The category.
      // RefdataCategory.withTransaction { status ->
      def cats = RefdataCategory.executeQuery('select c from RefdataCategory as c where lower(c.desc) = :desc', [desc: category_name.toLowerCase()]);
      def cat = null;

      if (cats.size() == 0) {
        // log.debug("Create new refdata category ${category_name}");
        cat = new RefdataCategory(desc: category_name, label: category_name)
        if (cat.save(failOnError: true)) {
        }
        else {
          log.error("Problem creating new category ${category_name}");
          cat.errors.each {
            log.error("Problem: ${it}");
          }
        }

        // log.debug("Create new refdataCategory(${category_name}) = ${cat.id}");
      }
      else if (cats.size() == 1) {
        cat = cats[0]
        // log.debug("Found existing category for ${category_name} : ${cat}");
        result = RefdataValue.findByOwnerAndValueIlike(cat, value)
      }
      else {
        throw new RuntimeException("Multiple matching refdata category names")
      }

      if (!result) {
        // Create and save a new refdata value.
        // log.info("Attempt to create new refdataValue(${category_name},${value},${sortkey})");
        result = new RefdataValue(owner: cat, value: value, sortKey: sortkey)
        if (result.save(failOnError: true)) {
        } else {
          // log.debug("Problem saving new refdata item");
          result.errors.each {
            log.error("Problem: ${it}");
          }
        }
      } else {
        // log.debug("Located existing refdata value.. ${value} ${result}");
        rdv_cache[rdv_cache_key] = result.id
      }
      // }
    }


    assert result != null

    // return the refdata value.
    result
  }


  static RefdataValue lookupOrCreate(String category_name, Map sortedValues) {
    for (def entry in sortedValues){
      lookupOrCreate(category_name, entry.getKey(), entry.getValue())
    }
  }

  static String getOID(category_name, value) {
    String result = null
    def cat = RefdataCategory.findByDesc(category_name);
    if (cat != null && value != null) {
      def v = RefdataValue.findByOwnerAndValueIlike(cat, value)
      if (v != null) {
        result = "wekb.RefdataValue:${v.id}"
      }
    }
  }

  static RefdataCategory construct(Map<String, Object> map) {

      String token = map.get('token')
      boolean hardData = new Boolean(map.get('hardData'))
      String desc_en = map.get('desc_en')
      String desc_de = map.get('desc_de')

      RefdataCategory rdc = RefdataCategory.findByDesc(token)
      //RefdataCategory rdc = RefdataCategory.findByDescIlike(token)

      if (!rdc) {
        static_logger.info("INFO: no match found; creating new refdata category for ( ${map})")
        rdc = new RefdataCategory(desc: token)
      }

      rdc.desc_de = desc_en ?: null
      rdc.desc_en = desc_de ?: null

      rdc.isHardData = hardData
      rdc.save(flush: true)

      rdc
  }

  Integer getValuesCount(){
    return values.size()
  }

  @Transient
  public String getDomainName() {
    return "Refdata Category"
  }

  public String getShowName() {
    return this.desc
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;

    def query = "from RefdataCategory as rc where (lower(rc.desc) like :value OR lower(rc.desc_de) like :value OR lower(rc.desc_en) like :value)"
    Map query_params = [value: "%${params.q.toLowerCase()}%"]

    ql = RefdataCategory.findAll(query, query_params, params)

    if ( ql ) {
      ql.sort {it.getI10n('desc')}.each { RefdataCategory refdataCategory ->
        result.add([id:"${refdataCategory.class.name}:${refdataCategory.id}", text:"${refdataCategory.getI10n('desc')}"])
      }
    }

    result
  }

}
