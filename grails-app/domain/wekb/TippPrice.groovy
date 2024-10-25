package wekb

import wekb.helper.BeanStore


class TippPrice {

  TitleInstancePackagePlatform tipp
  RefdataValue priceType
  RefdataValue currency
  Date startDate
  Date endDate
  Float price

  Date dateCreated
  Date lastUpdated

  static mapping = {
    id column: 'tp_id'
    version column: 'tp_version'

    tipp column: 'tp_tipp_fk', index: 'tp_tipp_fk_idx'
    priceType column: 'tp_type_fk', index: 'tp_type_idx'
    currency column: 'tp_currency_fk', index: 'tp_currency_idx'

    startDate column: 'tp_start_date', index: 'tp_start_date_idx'
    endDate column: 'tp_end_date', index: 'tp_end_date_idx'
    price column: 'tp_price'

    dateCreated     column: 'tp_date_created'
    lastUpdated     column: 'tp_last_updated'
  }

  static constraints = {
    startDate(nullable: false, blank: true)
    endDate(nullable: true, blank: true)

    lastUpdated (nullable: true)
    dateCreated (nullable: true)

    tipp(unique: ['tipp', 'priceType', 'currency'])
  }

  @Override
  int hashCode() {
    return tipp ? tipp.hashCode() : 0
    +priceType ? priceType.hashCode() : 0
    +currency ? currency.hashCode() : 0
    +startDate ? startDate.hashCode() : 0
    +endDate ? endDate.hashCode() : 0
    +price ? price.hashCode() : 0
  }

  @Override
  boolean equals(Object obj) {
    if (!TippPrice.isInstance(obj))
      return false
    TippPrice other = (TippPrice) obj
    if (this.tipp != null && other.tipp != null) {
      boolean eq = this.tipp == other.tipp
      if (!eq) {
        return false
      }
    }
    if (this.priceType != null && other.priceType != null) {
      if (!this.priceType?.equals(other.priceType)) {
        return false
      }
    }
    if (this.currency != null && other.currency != null) {
      if (!this.currency?.equals(other.currency)) {
        return false
      }
    }
    if (this.startDate != null && other.startDate != null) {
      if (!this.startDate?.equals(other.startDate)) {
        return false
      }
    }
    if (this.endDate != null && other.endDate != null) {
      if (!this.endDate?.equals(other.endDate)) {
        return false
      }
    }
    if (this.price != null && other.price != null) {
      if (this.price != other.price) {
        return false
      }
    }
    true
  }

  def beforeValidate (){
    log.debug("beforeValidate for ${this}")
    this.startDate = new Date()

  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    BeanStore.getCascadingUpdateService().update(this, lastUpdated)

  }

  String getOID(){
    "${this.class.name}:${id}"
  }

}
