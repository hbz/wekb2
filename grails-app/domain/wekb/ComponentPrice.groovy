package wekb



/**
 * ComponentPrice - variant prices for a component based on different scenarios.
 * Allow a package/tipp to hold multiple variant prices - EG list price for a normal subscription, list price for 
 * perpetual access, list price for one off or top-up access.
 * Requirements derived from Jisc DAC project - See owen stephens for more info.
 */
class ComponentPrice {


  def cascadingUpdateService

  KBComponent owner
  RefdataValue priceType  // Examples are list, list-perpetual, list, list-topup, etc
  RefdataValue currency // Currency for price
  Date startDate
  Date endDate
  Float price

  Date dateCreated
  Date lastUpdated

  static mapping = {
    owner column: 'cp_owner_component_fk', index: 'cp_owner_component_idx'
    priceType column: 'cp_type_fk', index: 'cp_type_idx'
    currency column: 'cp_currency_fk', index: 'cp_currency_idx'
    startDate column: 'cp_start_date', index: 'cp_start_date_idx'
    endDate column: 'cp_end_date', index: 'cp_end_date_idx'
    price column: 'cp_price'
    dateCreated     column: 'cp_date_created'
    lastUpdated     column: 'cp_last_updated'
  }

  static constraints = {
    owner(nullable: false)
    priceType(nullable: false, blank: true)
    currency(nullable: true, blank: true)
    startDate(nullable: false, blank: true)
    endDate(nullable: true, blank: true)
    price(nullable: true, blank: true)
    lastUpdated (nullable: true)
    dateCreated (nullable: true)
  }

  @Override
  int hashCode() {
    return owner ? owner.hashCode() : 0
    +priceType ? priceType.hashCode() : 0
    +currency ? currency.hashCode() : 0
    +startDate ? startDate.hashCode() : 0
    +endDate ? endDate.hashCode() : 0
    +price ? price.hashCode() : 0
  }

  @Override
  boolean equals(Object obj) {
    if (!ComponentPrice.isInstance(obj))
      return false
    ComponentPrice other = (ComponentPrice) obj
    if (this.owner != null && other.owner != null) {
      boolean eq = this.owner == other.owner
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
    cascadingUpdateService.update(this, dateCreated)

  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

}
