package wekb

import grails.plugin.springsecurity.SpringSecurityService
import wekb.auth.Role
import wekb.auth.User
import wekb.auth.UserRole
import wekb.utils.DateUtils
import wekb.helper.RCConstants
import wekb.helper.RDStore
import grails.core.GrailsClass
import grails.gorm.transactions.Transactional
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.springframework.security.access.annotation.Secured

import java.text.SimpleDateFormat

class AjaxHtmlController {

    GenericOIDService genericOIDService
    SpringSecurityService springSecurityService
    MessageService messageService
    AccessService accessService


    /**
     *  addToCollection : Used to create a form which will add a new object to a named collection within the target object.
     * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) Of the context object
     * @param __newObjectClass : The fully qualified class name of the instance to create
     * @param __recip : Optional - If set, then new_object.recip will point to __context
     * @param __addToColl : The name of the local set to which the new object should be added
     * @param All other parameters are taken to be property names on newObjectClass and used to init the new instance.
     */
    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addToCollection() {
        log.debug("AjaxController::addToCollection ${params}")
        User user = springSecurityService.currentUser
        def contextObj = resolveOID3(params.__context)
        def new_obj = null
        def errors = []
        GrailsClass domain_class = grailsApplication.getArtefact('Domain', params.__newObjectClass)

        if (domain_class) {
            if (contextObj) {
                def editable = accessService.checkEditableObject(contextObj, params)//checkEditable(contextObj)

                if (editable || contextObj.id == user.id) {
                    log.debug("Create a new instance of ${params.__newObjectClass}")

                    if (params.__newObjectClass == "wekb.ComponentVariantName") {

                        def norm_variant = TextUtils.normaliseString(params.variantName)

                        def existing_variants

                        if (contextObj instanceof Package) {
                            existing_variants = ComponentVariantName.findByNormVariantNameAndPkg(norm_variant, contextObj)
                        }
                        if (contextObj instanceof Org) {
                            existing_variants = ComponentVariantName.findByNormVariantNameAndOrg(norm_variant, contextObj)
                        }

                        if (existing_variants) {
                            log.debug("found dupes!")
                            errors.add(message(code: 'variantName.value.notUnique', default: 'This variant is already present in this list'))
                        } else {
                            log.debug("create new variantName")
                        }
                    }

                    if (params.__newObjectClass == "wekb.TitleInstancePackagePlatform") {

                        if (!params.title || params.title.size() == 0) {
                            log.debug("missing title for TIPP creation")
                            errors.add(message(code: 'tipp.title.nullable', default: 'Please provide a title for the TIPP'))
                        }

                        if (!params.hostPlatform || params.hostPlatform.size() == 0) {
                            log.debug("missing platform for TIPP creation")
                            errors.add(message(code: 'tipp.hostPlatform.nullable', default: 'Please provide a platform for the TIPP'))
                        }

                        if (!params.url || params.url.size() == 0) {
                            log.debug("missing url for TIPP creation")
                            errors.add(message(code: 'tipp.url.nullable', default: 'Please provide an url for the TIPP'))
                        }
                    }

                    if (errors.size() == 0) {
                        new_obj = domain_class.getClazz().getDeclaredConstructor().newInstance()
                        PersistentEntity pent = grailsApplication.mappingContext.getPersistentEntity(domain_class.fullName)

                        pent.getPersistentProperties().each { p -> // list of PersistentProperties
                            log.debug("${p.name} (assoc=${p instanceof Association}) (oneToMany=${p instanceof OneToMany}) (ManyToOne=${p instanceof ManyToOne}) (OneToOne=${p instanceof OneToOne})")
                            if (params[p.name] && p.name != 'format') {
                                if (p instanceof Association) {
                                    if (p instanceof ManyToOne || p instanceof OneToOne) {
                                        // Set ref property
                                        log.debug("set assoc ${p.name} to lookup of OID ${params[p.name]}")
                                        // if ( key == __new__ then we need to create a new instance )
                                        new_obj[p.name] = resolveOID3(params[p.name])
                                    } else {
                                        // Add to collection
                                        log.debug("add to collection ${p.name} for OID ${params[p.name]}")
                                        new_obj[p.name].add(resolveOID3(params[p.name]))
                                    }
                                } else {
                                    log.debug("checking for type of property -> ${p.type}")
                                    switch (p.type) {
                                        case Long.class:
                                            log.debug("Set simple prop ${p.name} = ${params[p.name]} (as long=${Long.parseLong(params[p.name])})")
                                            new_obj[p.name] = Long.parseLong(params[p.name])
                                            break

                                        case Date.class:
                                            def dateObj = params.date(p.name, 'yyyy-MM-dd')
                                            new_obj[p.name] = dateObj
                                            log.debug("Set simple prop ${p.name} = ${params[p.name]} (as date ${dateObj}))")
                                            break

                                        case Float.class:
                                            log.debug("Set simple prop ${p.name} = ${params[p.name]} (as float=${Float.valueOf(params[p.name])})")
                                            new_obj[p.name] = Float.valueOf(params[p.name])
                                            break

                                        default:
                                            log.debug("Default for type ${p.type}")
                                            log.debug("Set simple prop ${p.name} = ${params[p.name]}")
                                            new_obj[p.name] = params[p.name]
                                            break
                                    }
                                }
                            }
                        }

                        if (params.__refdataName && params.__refdataValue) {
                            log.debug("set refdata " + params.__refdataName + " for component ${contextObj}")
                            def refdata = resolveOID3(params.__refdataValue)
                            new_obj[params.__refdataName] = refdata
                        }

                        // Need to do the right thing depending on who owns the relationship. If new obj
                        // BelongsTo other, should be added to recip collection.
                        if (params.__recip) {
                            log.debug("Set reciprocal property ${params.__recip} to ${contextObj}")
                            new_obj[params.__recip] = contextObj
                            log.debug("Saving ${new_obj}")
                            if (new_obj.validate()) {
                                new_obj.save()
                                log.debug("Saved OK")
                                contextObj.save()
                            } else {
                                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
                            }
                        } else if (params.__addToColl) {
                            contextObj[params.__addToColl].add(new_obj)
                            log.debug("Saving ${new_obj}")

                            if (new_obj.validate()) {
                                new_obj.save()
                                log.debug("New Object Saved OK")
                            } else {
                                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
                            }

                            if (contextObj.validate()) {
                                contextObj.save()
                                log.debug("Context Object Saved OK")
                            } else {
                                errors.addAll(messageService.processValidationErrorsToListForFlashError(contextObj.errors, request.locale))
                            }
                        } else {
                            // Stand alone object.. Save it!
                            log.debug("Saving stand alone reference object")
                            if (new_obj.validate()) {
                                new_obj.save()
                                log.debug("Saved OK (${new_obj.class.name} ${new_obj.id})")
                            } else {
                                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
                            }
                        }
                    }
                } else {
                    log.debug("Located instance of context class with oid ${params.__context} is not editable.")
                    flash.error = message(code: 'component.addToList.denied.label')
                }
            } else if (!contextObj) {
                log.debug("Unable to locate instance of context class with oid ${params.__context}")
                flash.error = message(code: 'component.context.notFound.label')
            }
        } else {
            if (!domain_class) {
                log.error("Unable to lookup domain class ${params.__newObjectClass}")
                flash.error = message(code: 'component.classNotFound.label', args: [params.__newObjectClass])
            } else {
                flash.error = message(code: 'component.create.denied.label', args: [params.__newObjectClass])
                log.error("No permission to create an object of domain class ${params.__newObjectClass}")
            }
        }

        if (errors.size() > 0) {
            flash.error = errors
        }


        if (new_obj && params.__showNew && errors.size() == 0) {
            redirect(controller: 'resource', action: 'show', id: new_obj.class.name + ':' + new_obj.id, params: [activeTab: params.activeTab])
        } else {
            def redirect_to = request.getHeader('referer')

            if (params.activeTab && params.activeTab.length() > 0) {
                redirect_to = "${redirect_to}?activeTab=${params.tab}"
            }
            redirect(url: redirect_to)
        }
    }

    /**
     *  addToStdCollection : Used to add an existing object to a named collection that is not mapped through a join object.
     * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
     * @param __relatedObject : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the object to be added to the list
     * @param __property : The property name of the collection to which the object should be added
     */
    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addToStdCollection() {
        log.debug("addToStdCollection(${params})")
        // Adds a link to a collection that is not mapped through a join object
        def contextObj = resolveOID3(params.__context)
        def user = springSecurityService.currentUser
        def relatedObj = resolveOID3(params.__relatedObject)
        def result = ['result': 'OK', 'params': params]
        if (relatedObj != null && contextObj != null) {
            def editable = checkEditable(contextObj)

            if (editable || user.id == contextObj.id) {
                if (!contextObj["${params.__property}"].contains(relatedObj)) {
                    contextObj["${params.__property}"].add(relatedObj)
                    contextObj.save()
                    log.debug("Saved: ${contextObj.id}")
                    result.context = contextObj
                } else {
                    flash.error = "Object is already present in this list!"
                    log.debug("Tried to add the same object twice!")
                }
            } else {
                flash.error = message(code: 'component.list.add.denied.label')
                log.debug("context object not editable.")
            }
        } else if (!contextObj) {
            flash.error = message(code: 'component.context.notFound.label')
        } else if (!relatedObj) {
            flash.error = message(code: 'component.listItem.notFound.label')
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])
    }

    /**
     *  unlinkManyToMany : Used to remove an object from a named collection.
     * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
     * @param __itemToRemove : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the object to be removed from the list
     * @param __property : The property name of the collection from which the object should be removed from
     * @param __otherEnd : The property name from the side of the object to be removed
     */
    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def unlinkManyToMany() {
        log.debug("unlinkManyToMany(${params})")
        def contextObj = resolveOID3(params.__context)
        def user = springSecurityService.currentUser
        def result = ['result': 'OK', 'params': params]
        if (contextObj) {
            def editable = checkEditable(contextObj)

            if (editable || contextObj.id == user.id) {
                def item_to_remove = resolveOID3(params.__itemToRemove)
                if (item_to_remove) {
                    log.debug("${params}")
                    log.debug("removing: ${item_to_remove} from ${params.__property} for ${contextObj}")

                    def remove_result = contextObj[params.__property].remove(item_to_remove)

                    log.debug("remove successful?: ${remove_result}")
                    log.debug("child ${item_to_remove} removed: " + contextObj[params.__property])

                    if (contextObj.save(failOnError: true)) {
                        log.debug("Saved context object ${contextObj.class.name}")
                    } else {
                        flash.error = messageService.processValidationErrorsToListForFlashError(contextObj.errors, request.locale)

                    }

                    if (item_to_remove.hasProperty('fromComponent') && item_to_remove.fromComponent == contextObj) {
                        item_to_remove.delete()
                    } else {

                        if (params.__otherEnd && item_to_remove[params.__otherEnd] != null) {
                            log.debug("remove parent: " + item_to_remove[params.__otherEnd])
                            //item_to_remove.setParent(null)
                            item_to_remove[params.__otherEnd] = null //this seems to fail
                            log.debug("parent removed: " + item_to_remove[params.__otherEnd])
                        }
                        if (!item_to_remove.validate()) {
                            flash.error = messageService.processValidationErrorsToListForFlashError(item_to_remove.errors, request.locale)
                        } else {
                            item_to_remove.save()
                        }
                    }
                } else {
                    log.error("Unable to resolve item to remove : ${params.__itemToRemove}")
                    flash.error(code: 'component.listItem.notFound.label')
                }
            } else {
                flash.error = message(code: 'component.list.remove.denied.label')
                log.debug("Located instance of context class with oid ${params.__context} is not editable.")
            }
        } else {
            flash.error = message(code: 'component.context.notFound.label')
            log.debug("Unable to locate instance of context class with oid ${params.__context}")
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def unlinkManyToOne() {
        log.debug("unlinkManyToOne(${params})")
        def contextObj = resolveOID3(params.__context)
        def result = ['result': 'OK', 'params': params]
        if (contextObj) {
            def editable = checkEditable(contextObj)

            if (editable) {
                contextObj[params.__property] = null

                if (contextObj.save(failOnError: true)) {
                    log.debug("Saved context object ${contextObj.class.name}")
                } else {
                    flash.error = messageService.processValidationErrorsToListForFlashError(contextObj.errors, request.locale)
                }
            } else {
                flash.error = message(code: 'component.delete.denied.label')
                log.debug("Located instance of context class with oid ${params.__context} is not editable.")
            }
        } else {
            flash.error = message(code: 'component.context.notFound.label')
            log.debug("Unable to locate instance of context class with oid ${params.__context}")
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])
    }

    /**
     *  delete : Used to delete a domain class object.
     * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
     */
    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        log.debug("delete(${params})")
        // Adds a link to a collection that is not mapped through a join object
        def contextObj = resolveOID3(params.__context)
        def user = springSecurityService.currentUser
        def result = ['result': 'OK', 'params': params]

        if (contextObj) {
            def editable = checkEditable(contextObj)

            if (editable) {
                if (contextObj.respondsTo('deleteSoft')) {
                    contextObj.deleteSoft()
                } else {
                    contextObj.delete()
                }
                log.debug("Item deleted.")
            } else {
                flash.error = message(code: 'component.delete.denied.label')
                log.debug("Located instance of context class with oid ${params.__context} is not editable.")
            }
        } else {
            flash.error = message(code: 'component.notFound.label', args: [params.__context])
            log.debug("Unable to locate instance of context class with oid ${params.__context}")
        }

        def redirect_to = request.getHeader('referer')

        if (params.redirect) {
            redirect(url: params.redirect)
        } else if ((params.activeTab) && (params.activeTab.length() > 0)) {
            redirect(url: redirect_to, params: [activeTab: params.activeTab])
        } else {
            redirect(url: redirect_to)
        }
    }

    private def resolveOID3(oid) {
        def oid_components = oid.split(':')
        def result = null
        def domain_class = null
        domain_class = grailsApplication.getArtefact('Domain', oid_components[0])
        if (domain_class) {
            if (oid_components.size() == 2) {
                if (oid_components[1] == '__new__') {
                    result = domain_class.getClazz().refdataCreate(oid_components)
                    log.debug("Result of create ${oid} is ${result}")
                } else {
                    result = domain_class.getClazz().get(oid_components[1])
                }
            } else {
                log.debug("Could not retrieve object. No ID provided.")
            }
        } else {
            log.debug("resolve OID failed to identify a domain class. Input was ${oid_components}")
        }
        result
    }

    /**
     *  editableSetValue : Used to set a primitive property value.
     * @param pk : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
     * @param type : Used for date parsing with value 'date'
     * @param dateFormat : Used for overriding the default date format ('yyyy-MM-dd')
     * @param name : The name of the property to be changed
     * @param value : The new value for the property
     */
    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def editableSetValue() {
        log.debug("editableSetValue ${params}")
        def user = springSecurityService.currentUser
        def target_object = genericOIDService.resolveOID(params.pk)

        def result = ['result': 'OK', 'params': params]
        def errors = [:]
        if (target_object) {
            def editable = checkEditable(target_object)

            if (editable || target_object == user) {
                if (params.type == 'date') {
                    SimpleDateFormat sdf = DateUtils.getSDF_NoTime()
                    def backup = target_object."${params.name}"

                    try {
                        if (params.value && params.value.size() > 0) {
                            // parse new date
                            def parsed_date = sdf.parse(params.value)
                            target_object."${params.name}" = parsed_date
                        } else {
                            // delete existing date
                            target_object."${params.name}" = null
                        }
                        target_object.save(failOnError: true)
                    }
                    catch (Exception e) {
                        target_object."${params.name}" = backup
                        log.error(e.toString())
                    }

                    //target_object."${params.name}" = params.date('value',params.dateFormat ?: 'yyyy-MM-dd')
                } else if (params.type == 'boolean') {
                    target_object."${params.name}" = params.boolean('value')
                } else if (params.name == 'uuid' || params.name == 'password') {
                    errors[params.name] = "This property is not editable."
                } else {
                    def binding_properties = [:]
                    def new_val = params.value?.trim() ?: null

                    binding_properties[params.name] = new_val
                    bindData(target_object, binding_properties)
                }

                if (target_object.validate()) {
                    target_object.save()
                } else {
                    errors = messageService.processValidationErrors(target_object.errors, request.locale)
                }
            } else {
                errors['global'] = [[message: "Object ${target_object} is not editable.".toString()]]
                log.debug("Object ${target_object} is not editable.")
            }
        } else {
            errors['global'] = [[message: "Not able to resolve object from ${params.pk}.".toString()]]
            log.debug("Object ${target_object} could not be resolved.")
        }

        def resp = null
        if (errors.size() == 0) {
            resp = params.value
        } else {
            log.error("Error msg: ${params.name} (${errors})")

            def error_obj = errors[params.name] ? errors[params.name][0] : errors['global'][0]

            resp = error_obj.message
            response.setContentType('text/plain;charset=UTF-8')
            response.status = 400
            render resp
        }
    }

    private boolean checkEditable(obj) {
        boolean editable = accessService.checkEditableObject(obj, params)
        editable
    }

    /**
     *  genericSetRel : Used to set a complex property value.
     * @param pk : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
     * @param name : The name of the property to be changed
     * @param value : The OID ([FullyQualifiedClassName]:[PrimaryKey]) of the object to link
     */

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def genericSetRel() {
        // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
        // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
        log.debug("genericSetRel ${params}")
        def user = springSecurityService.currentUser
        def target = genericOIDService.resolveOID(params.pk)
        def value = null

        if (params.type == 'boolean') {
            value = params.boolean('value')
        } else {
            value = genericOIDService.resolveOID(params.value)
        }

        def result = ['result': 'OK']

        if (target != null) {
            def editable = checkEditable(target)

            if (editable) {
                // def binding_properties = [ "${params.name}":value ]
                log.debug("Binding: ${params.name} into ${target} - a ${target.class.name}")
                // bindData(target, binding_properties)
                target[params.name] = value
                log.debug("Saving... after assignment ${params.name} = ${target[params.name]}")

                if (target.validate()) {
                    target = target.merge(failOnError: true)

                    if (params.resultProp) {
                        result = value ? value[params.resultProp] : ''
                    }

                } else {
                    log.debug("Problem saving.. ${target.errors}")
                    result.errors = messageService.processValidationErrors(target.errors, request.locale)
                }
            } else {
                log.debug("Target is not editable!")
                result.errors = ["Not able to edit this property!"]
            }
        } else {
            log.debug("Target not found!")
            result.errors = ["Unable to locate intended target object!"]
        }

        def redirect_to = request.getHeader('referer')

        if (params.redirect) {
            redirect(url: params.redirect)
        } else if ((params.activeTab) && (params.activeTab.length() > 0)) {
            redirect(url: redirect_to, params: [activeTab: params.activeTab])
        } else {
            redirect(url: redirect_to)
        }
    }

    /**
     *  addIdentifier : Used to add an identifier to a list.
     * @param __context : The OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
     * @param identifierNamespace : The OID ([FullyQualifiedClassName]:[PrimaryKey]) of the identifier namespace
     * @param identifierValue : The value of the identifier to link
     */

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addIdentifier() {
        log.debug("addIdentifier - ${params}")
        def result = ['result': 'OK', 'params': params]
        def owner = genericOIDService.resolveOID(params.__context)
        def identifier_instance = null
        // Check identifier namespace present, and identifier value valid for that namespace
        if ((params.identifierNamespace?.trim()) &&
                (params.identifierValue?.trim()) &&
                (params.__context?.trim())) {
            def ns = genericOIDService.resolveOID(params.identifierNamespace)

            if ((ns != null) && (owner != null)) {
                def editable = checkEditable(owner)

                if (editable) {
                    // Lookup or create Identifier
                    try {
                        String attr = Identifier.getAttributeName(owner)
                        def ident = Identifier.executeQuery(
                                'select ident from Identifier ident where ident.value = :val and ident.namespace = :namespace and ident.' + attr + ' = :owner order by ident.id',
                                [val: params.identifierValue, namespace: ns, owner: owner])

                        if (ident) {
                            flash.error = message(code: 'identifier.no.unique.by.component')
                        } else if (!ident) {
                            ident = new Identifier(namespace: ns, value: params.identifierValue)
                            ident.setReference(owner)
                            boolean success = ident.save() //needed to trigger afterInsert() temp solution
                            if (success) {
                                flash.success = message(code: 'identifier.create.success')
                            } else {
                                flash.error = message(code: 'identifier.create.fail')
                            }
                        }
                    }
                    catch (grails.validation.ValidationException ve) {

                        log.debug("${ve}")
                        flash.error = message(code: 'identifier.value.illegalIdForm')
                    }
                } else {
                    flash.error = message(code: 'component.addToList.denied.label')
                }
            } else {
                flash.error = message(code: 'identifier.create.error')
                log.debug("could not create identifier!")
            }
        }
        redirect(controller: 'resource', action: 'show', id: owner.class.name + ':' + owner.id, params: [activeTab: params.activeTab])
    }


    /**
     *  authorizeVariant : Used to replace the name of a component by one of its existing variant names.
     * @param id : The id of the variant name
     */

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def authorizeVariant() {
        log.debug("${params}")
        def result = ['result': 'OK', 'params': params]
        ComponentVariantName variant = ComponentVariantName.get(params.id)
        def owner = variant.getReference()
        if (variant != null) {

            def editable = checkEditable(owner)

            if (editable) {
                // Does the current owner.name exist in a variant? If not, we should create one so we don't loose the info
                def current_name_as_variant = owner.variantNames.find { it.variantName == owner.name }

                result.owner = "${owner.getOID()}"

                if (current_name_as_variant == null) {
                    log.debug("No variant name found for current name: ${owner.name} ")
                    def variant_name = owner.getId()

                    if (variant.owner.name) {
                        variant_name = owner.name
                    } else if (owner?.respondsTo('getShowName') && owner.getShowName()) {
                        variant_name = owner.getShowName()?.trim()
                    } else if (owner?.respondsTo('getName')) {
                        variant_name = owner?.getName()?.trim()
                    }

                    def new_variant = new ComponentVariantName(owner: owner, variantName: variant_name).save()

                } else {
                    log.debug("Found existing variant name: ${current_name_as_variant}")
                }

                variant.variantType = RefdataCategory.lookupOrCreate(RCConstants.COMPONENT_VARIANTNAME_VARIANT_TYPE, 'Authorized')
                owner.name = variant.variantName

                if (owner.validate()) {
                    owner.save()
                    result.new_name = variant.owner.name
                } else {
                    result.message = "This name already belongs to another component of the same type!"
                    flash.error = message(code: 'variantName.authorize.notUnique')
                }
            } else {
                result.message = "No permission to edit variants for this object!"
                flash.error = message(code: 'variantName.owner.denied')
            }
        } else if (!variant) {
            result.message = "Variant with id ${params.id} not found!".toString()
            def vname = message(code: 'variantName.label')
            flash.message = message(code: 'default.not.found.message', args: [vname, params.id])
        }

        redirect(controller: 'resource', action: 'show', id: owner.class.name + ':' + owner.id, params: [activeTab: params.activeTab])

    }

    /**
     *  deleteVariant : Used to delete a variant name of a component.
     * @param id : The id of the variant name
     */

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def deleteVariant() {
        log.debug("${params}")
        def result = ['result': 'OK', 'params': params]
        ComponentVariantName variant = ComponentVariantName.get(params.id)
        def variantOwner = variant.getReference()

        if (variant != null) {
            def editable = checkEditable(variantOwner)

            if (editable) {
                def variantName = variant.variantName

                variant.delete()
                variantOwner.save()

                result.owner_oid = "${variantOwner.class.name}:${variantOwner.id}"
                result.deleted_variant = "${variantName}"
            } else {
                result.message = "No permission to edit variants for this object!"
                flash.error = message(code: 'variantName.owner.denied')
            }
        } else if (!variant) {
            def vname = message(code: 'variantName.label')
            flash.error = message(code: 'default.not.found.message', args: [vname, params.id])
            result.message = "Variant with id ${params.id} not found!".toString()
        }

        redirect(controller: 'resource', action: 'show', id: variantOwner.class.name + ':' + variantOwner.id, params: [activeTab: params.activeTab])
    }

    /**
     *  deleteCoverageStatement : Used to delete a TIPPCoverageStatement.
     * @param id : The id of the coverage statement object
     */

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def deleteCoverageStatement() {
        log.debug("${params}")
        def result = ['result': 'OK', 'params': params]
        TIPPCoverageStatement tcs = TIPPCoverageStatement.get(params.id)
        TitleInstancePackagePlatform tipp = tcs.tipp

        if (tcs != null) {
            def editable = checkEditable(tipp)

            if (editable) {
                tcs.delete()
            } else {
                result.message = "This TIPP is not editable!"
                flash.error = message(code: 'tipp.coverage.denied.label')
            }
        } else if (!tcs) {
            def vname = message(code: 'TIPPCoverageStatement.label')
            result.message = "TIPPCoverageStatement with id ${params.id} not found!".toString()
            flash.error = message(code: 'default.not.found.message', args: [vname, params.id])
        }

        redirect(controller: 'resource', action: 'show', id: tipp.class.name + ':' + tipp.id, params: [activeTab: params.activeTab])
    }

    /**
     *  deletePrice : Used to delete a TippPrice from a TitleInstance.
     * @param id : The id of the TippPrice
     */

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def deletePrice() {
        def result = ['result': "OK", 'params': params]
        TippPrice c = TippPrice.get(params.id)
        TitleInstancePackagePlatform contextObj = c.tipp

        if (c) {
            def editable = checkEditable(c.tipp)

            if (editable) {
                log.debug("Delete Price..")
                c.delete()
            }
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])

    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def deleteLanguage() {
        log.debug("${params}")
        def result = ['result': 'OK', 'params': params]
        ComponentLanguage componentLanguage = ComponentLanguage.get(params.id)
        TitleInstancePackagePlatform contextObj = componentLanguage.tipp

        if (componentLanguage != null) {
            def editable = checkEditable(componentLanguage.tipp)

            if (editable) {
                componentLanguage.delete()
            } else {
                result.message = "No permission to edit language for this object!"
                flash.error = "No permission to edit language for this object!"
            }
        } else if (!componentLanguage) {
            flash.error = message(code: 'default.not.found.message', args: ["Language", params.id])
            result.message = "Language with id ${params.id} not found!".toString()
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addContact() {
        log.debug("addContact - ${params}")
        def result = ['result': 'OK', 'params': params]
        Contact contact = null
        if (params.content?.trim()) {
            String content = params.content
            RefdataValue contentType = genericOIDService.resolveOID(params.contentType)
            RefdataValue language = genericOIDService.resolveOID(params.language)
            RefdataValue type = genericOIDService.resolveOID(params.type)
            Org owner = genericOIDService.resolveOID(params.__context)
            def editable = checkEditable(owner)
            if (editable) {
                if (contentType == RDStore.CONTACT_CONTENT_TYPE_EMAIL) {
                    if (content ==~ /[_A-Za-z0-9-]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})/) {
                        contact = new Contact(org: owner, content: content, contentType: contentType, language: language, type: type)
                        contact.save()
                    } else {
                        flash.error = message(code: 'contact.email.validation.fail')
                    }
                } else {
                    contact = new Contact(org: owner, content: content, contentType: contentType, language: language, type: type)
                    contact.save()
                }
            } else {
                flash.error = message(code: 'component.addToList.denied.label')
            }
        }

        redirect(url: (request.getHeader('referer')))
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addUserToCuratoryGroup() {
        log.debug("addUserToCuratoryGroup(${params})")
        User contextObj = resolveOID3(params.__user)
        def user = springSecurityService.currentUser
        CuratoryGroup curatoryGroup = resolveOID3(params.__curatoryGroup)
        if (curatoryGroup != null && contextObj != null) {
            if (user.isAdmin()) {
                if (!CuratoryGroupUser.findByUserAndCuratoryGroup(contextObj, curatoryGroup)) {
                    CuratoryGroupUser curatoryGroupUser = new CuratoryGroupUser(user: contextObj, curatoryGroup: curatoryGroup)
                    curatoryGroupUser.save()
                } else {
                    flash.error = "User has already this curatory group!"
                }
            } else {
                flash.error = message(code: 'component.list.add.denied.label')
            }
        } else if (!contextObj) {
            flash.error = message(code: 'component.context.notFound.label')
        } else if (!curatoryGroup) {
            flash.error = message(code: 'component.listItem.notFound.label')
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addRoleToUser() {
        log.debug("addRoleToUser(${params})")
        User contextObj = resolveOID3(params.__user)
        def user = springSecurityService.currentUser
        Role role = resolveOID3(params.__role)
        if (role != null && contextObj != null) {
            if (user.isAdmin()) {
                if (!UserRole.findByUserAndRole(contextObj, role)) {
                    UserRole userRole = new UserRole(user: contextObj, role: role)
                    userRole.save()
                } else {
                    flash.error = "User has already this role!"
                }
            } else {
                flash.error = message(code: 'default.noPermissons')
            }
        } else if (!contextObj) {
            flash.error = message(code: 'component.context.notFound.label')
        } else if (!role) {
            flash.error = message(code: 'component.listItem.notFound.label')
        }

        redirect(controller: 'resource', action: 'show', id: contextObj.class.name + ':' + contextObj.id, params: [activeTab: params.activeTab])
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def removeRoleFromUser() {
        log.debug("removeRoleFromUser(${params})")
        User userObject = User.get(params.us_Id)
        Role roleObject = Role.get(params.rol_Id)
        UserRole userRole = UserRole.findByUserAndRole(userObject, roleObject)
        def user = springSecurityService.currentUser
        if (userRole) {
            if (user.isAdmin()) {
                userRole.delete()
            } else {
                flash.error = message(code: 'default.noPermissons')
            }
        }

        redirect(controller: 'resource', action: 'show', id: userObject.class.name + ':' + userObject.id, params: [activeTab: params.activeTab])
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def removeCuratoryGroupFromUser() {
        log.debug("removeRoleFromUser(${params})")
        User userObject = User.get(params.us_Id)
        CuratoryGroup curatoryGroupObject = CuratoryGroup.get(params.cur_Id)
        CuratoryGroupUser curatoryGroupUser = CuratoryGroupUser.findByUserAndCuratoryGroup(userObject, curatoryGroupObject)
        def user = springSecurityService.currentUser
        if (curatoryGroupUser) {
            if (user.isAdmin()) {
                curatoryGroupUser.delete()
            } else {
                flash.error = message(code: 'default.noPermissons')
            }
        }

        redirect(controller: 'resource', action: 'show', id: userObject.class.name + ':' + userObject.id, params: [activeTab: params.activeTab])
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def removeCuratoryGroupFromObject() {
        log.debug("removeCuratoryGroup(${params})")
        CuratoryGroup curatoryGroup = resolveOID3(params.__curatoryGroup)
        boolean fail = false

        if (params.__contextObjectClass) {
            if (curatoryGroup) {
                def editable = accessService.checkEditableObject(curatoryGroup, params)

                if (editable) {
                    switch (params.__contextObjectClass) {
                        case KbartSource.class.name:
                            KbartSource kbartSource = KbartSource.findById(params.contextObject)
                            if (kbartSource) {
                                CuratoryGroupKbartSource curatoryGroupKbartSource = CuratoryGroupKbartSource.findByKbartSourceAndCuratoryGroup(kbartSource, curatoryGroup)
                                if (curatoryGroupKbartSource) {
                                    curatoryGroupKbartSource.delete()
                                }
                            } else {
                                fail = true
                            }
                            break
                        case Org.class.name:
                            Org org = Org.findById(params.contextObject)
                            if (org) {
                                CuratoryGroupOrg curatoryGroupOrg = CuratoryGroupOrg.findByOrgAndCuratoryGroup(org, curatoryGroup)
                                if (curatoryGroupOrg) {
                                    curatoryGroupOrg.delete()
                                }
                            } else {
                                fail = true
                            }
                            break
                        case Package.class.name:
                            Package pkg = Package.findById(params.contextObject)
                            if (pkg) {
                                CuratoryGroupPackage curatoryGroupPackage = CuratoryGroupPackage.findByPkgAndCuratoryGroup(pkg, curatoryGroup)
                                if (curatoryGroupPackage) {
                                    curatoryGroupPackage.delete()
                                }
                            } else {
                                fail = true
                            }
                            break
                        case Platform.class.name:
                            Platform platform = Platform.findById(params.contextObject)
                            if (platform) {
                                CuratoryGroupPlatform curatoryGroupPlatform = CuratoryGroupPlatform.findByPlatformAndCuratoryGroup(platform, curatoryGroup)
                                if (curatoryGroupPlatform) {
                                    curatoryGroupPlatform.delete()
                                }
                            } else {
                                fail = true
                            }
                            break
                    }
                } else {
                    flash.error = message(code: 'default.noPermissons')
                }
            } else {
                flash.error = message(code: 'component.notFound.forLink')
            }
        } else {
            fail = true
        }

        if (fail) {
            flash.error = message(code: 'default.action.fail')
        }

        redirect(url: request.getHeader('referer'))
    }

    @Transactional
    @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
    def addCuratoryGroupToObject() {
        log.debug("addUserToCuratoryGroup(${params})")
        CuratoryGroup curatoryGroup = resolveOID3(params.__curatoryGroup)
        boolean fail = false

        if (params.__contextObjectClass) {
            if (curatoryGroup) {
                def editable = accessService.checkEditableObject(curatoryGroup, params)

                if (editable) {
                    switch (params.__contextObjectClass) {
                        case KbartSource.class.name:
                            KbartSource kbartSource = KbartSource.findById(params.contextObject)
                            if (kbartSource) {
                                CuratoryGroupKbartSource curatoryGroupKbartSource = new CuratoryGroupKbartSource(kbartSource: kbartSource, curatoryGroup: curatoryGroup)
                                if (!curatoryGroupKbartSource.save()) {
                                    fail = true
                                }
                            } else {
                                fail = true
                            }
                            break
                        case Org.class.name:
                            Org org = Org.findById(params.contextObject)
                            if (org) {
                                CuratoryGroupOrg curatoryGroupOrg = new CuratoryGroupOrg(org: org, curatoryGroup: curatoryGroup)
                                if (!curatoryGroupOrg.save()) {
                                    fail = true
                                }
                            } else {
                                fail = true
                            }
                            break
                        case Package.class.name:
                            Package pkg = Package.findById(params.contextObject)
                            if (pkg) {
                                CuratoryGroupPackage curatoryGroupPackage = new CuratoryGroupPackage(pkg: pkg, curatoryGroup: curatoryGroup)
                                if (!curatoryGroupPackage.save()) {
                                    fail = true
                                }
                            } else {
                                fail = true
                            }
                            break
                        case Platform.class.name:
                            Platform platform = Platform.findById(params.contextObject)
                            if (platform) {
                                CuratoryGroupPlatform curatoryGroupPlatform = new CuratoryGroupPlatform(platform: platform, curatoryGroup: curatoryGroup)
                                if (!curatoryGroupPlatform.save()) {
                                    fail = true
                                }
                            } else {
                                fail = true
                            }
                            break
                    }
                } else {
                    flash.error = message(code: 'default.noPermissons')
                }
            } else {
                flash.error = message(code: 'component.notFound.forLink')
            }
        } else {
            fail = true
        }

        if (fail) {
            flash.error = message(code: 'default.action.fail')
        }

        redirect(url: request.getHeader('referer'))
    }

}
