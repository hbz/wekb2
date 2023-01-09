package wekb

import com.k_int.ClassUtils
import grails.plugin.springsecurity.SpringSecurityUtils
import org.gokb.cred.RefdataCategory
import org.gokb.cred.User
import org.springframework.context.i18n.LocaleContextHolder

import java.text.NumberFormat
import java.text.SimpleDateFormat

class SemanticInplaceTagLib {
    static namespace = "semui"

    def genericOIDService
    def springSecurityService

    /*@Deprecated
    private boolean checkEditable(attrs, body, out) {

        // See if there is an owner attribute on the request - owner will be the domain object asking to be edited.
        def user = springSecurityService.currentUser
        def owner = attrs.owner ? ClassUtils.deproxy(attrs.owner) : null
        def baseClass = attrs.baseClass ? grailsApplication.getArtefact("Domain", attrs.baseClass)?.clazz : null

        boolean cur = request.curator != null ? request.curator.size() > 0 : true

        // Default editable value.
        boolean tl_editable = owner?.isEditable() ?: (params.curationOverride == 'true' && user.isAdmin())

        if (owner?.class?.name == 'org.gokb.cred.User') {
            tl_editable = user.equals(owner)
        }

        if (tl_editable && owner?.respondsTo('getCuratoryGroups')) {
            tl_editable = (cur || (user.hasRole("ROLE_ADMIN") && params.curationOverride == "true"))
        }

        if (!tl_editable && !owner?.respondsTo('getCuratoryGroups') && baseClass?.isTypeAdministerable()) {
            tl_editable = true
        }

*//*    if ( !tl_editable && owner?.class?.name == 'org.gokb.cred.Combo' ) {
      tl_editable = owner.fromComponent.isEditable()
    }*//*

        if (!tl_editable && attrs.editable) {
            tl_editable = attrs.editable
        }

        if (attrs.overWriteEditable != null) {
            tl_editable = attrs.overWriteEditable
        }

        if (!tl_editable && SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')) {
            tl_editable = true
        }

        // If not editable then we should output as value only and return the value.
        if (!tl_editable) {
            def content = (owner?."${attrs.field}" ? renderObjectValue(owner."${attrs.field}") : body()?.trim())
            out << "<span class='readonly${content ? '' : ' editable-empty'}' title='This ${owner?.respondsTo('getNiceName') ? owner.getNiceName() : 'component'} is read only.' >${content ?: 'Empty'}</span>"
        }

        tl_editable
    }*/

   /* @Deprecated
    private boolean checkViewable(attrs, body, out) {

        // See if there is an baseClass attribute on the request - baseClass will be the domain class asking to be searched.
        def baseClass = attrs.baseClass ? grailsApplication.getArtefact("Domain", attrs.baseClass)?.clazz : null
        def owner = attrs.owner ? ClassUtils.deproxy(attrs.owner) : null
        def tl_viewable = false

        tl_viewable = baseClass.isTypeReadable()

        // If not editable then we should output as value only and return the value.
        if (!tl_viewable) {
            def content = (owner?."${attrs.field}" ? renderObjectValue(owner."${attrs.field}") : body()?.trim())
            out << "<span class='readonly${content ? '' : ' editable-empty'}' title='This ${baseClass?.respondsTo('getNiceName') ? baseClass.getNiceName() : 'component'} is not searchable.' >${content ?: 'Empty'}</span>"
        }

        tl_viewable
    }*/

    def xEditable = { attrs, body ->

        boolean editable = isEditable(request.getAttribute('editable'), attrs.overwriteEditable)

        if (editable) {
            def owner = ClassUtils.deproxy(attrs.owner)

            def oid = owner.id != null ? "${owner.class.name}:${owner.id}" : ''
            def id = attrs.id ?: "${oid}:${attrs.field}"
            def dformat = attrs."data-format" ?: 'yyyy-mm-dd'

            // Default the format.

            String default_empty = "Edit"
            String emptyText = attrs.emptytext ? " data-emptytext=\"${attrs.emptytext}\"" : " data-emptytext=\"${default_empty}\""

            out << "<span id=\"${id}\" class=\"xEditableValue ${attrs.class ?: ''} ${attrs.type == 'date' ? 'date' : ''}\""

            if (attrs.inputclass) {
                out << " data-inputclass=\"${attrs.inputclass}\""
            }
            if (oid && (oid != '')) out << " data-pk=\"${oid}\""
            out << " data-name=\"${attrs.field}\""

/*      // SO: fix for FF not honouring no-wrap css.
      if ((attrs.type ?: 'textarea') == 'textarea') {
        out << " data-tpl=\"${'<textarea wrap=\'off\'></textarea>'.encodeAsHTML()}\""
      }*/

            def update_link = null
            switch (attrs.type) {
                case 'date':
                    update_link = createLink(controller: 'ajaxSupport', action: 'editableSetValue', params: [type: 'date', curationOverride: params.curationOverride])
                    out << " data-type='text' data-format='YYYY-MM-DD' data-inputclass='form-control form-date'  data-viewformat='yyyy-mm-dd'"
                    def dv = attrs."data-value"

                    if (!dv) {
                        if (owner[attrs.field]) {

                            // Date format.
                            def sdf = new java.text.SimpleDateFormat(dformat.replace('mm', 'MM'))
                            dv = sdf.format(owner[attrs.field])
                        } else {
                            dv = ""
                        }
                    }

                    out << " data-value='${dv}'"

                    break;
                default:
                    update_link = createLink(controller: 'ajaxSupport', action: 'editableSetValue', params: [curationOverride: params.curationOverride])
                    out << " data-type=\"${attrs.type ?: 'text'}\""
                    break;
            }

            out << " data-url=\"${update_link}\" "

            if (attrs.validation) {
                out << " data-validation=\"${attrs.validation}\" "
            }
            if (attrs.required) {
                out << " data-required=\"${attrs.required}\" "
            }
            if (attrs.maxlength) {
                out << " data-maxlength=\"${attrs.maxlength}\" "
            }

            out << ">"

            if (body) {
                out << body()
            } else {
                String content = (attrs.owner?."${attrs.field}" ? renderObjectValue(attrs.owner."${attrs.field}") : "")
                if(content){
                    out << content
                }else {
                    out << ""
                }
            }
            out << "</span>"
        } else {
            out << "<span class=\"${attrs.class ?: ''}\">"
            if (body) {
                out << body()
            } else {
                String content = (attrs.owner?."${attrs.field}" ? renderObjectValue(attrs.owner."${attrs.field}") : "")
                if(content){
                    out << content
                }else {
                    out << "Empty"
                }
            }
            out << '</span>'
        }

        if (attrs.outGoingLink && attrs.field && attrs.owner[attrs.field]) {
            String url = attrs.owner[attrs.field] ? (attrs.owner[attrs.field].startsWith('http') ? attrs.owner[attrs.field] : ('http://' + attrs.owner[attrs.field])) : ""
            if(url) {
                out << '&nbsp;<a aria-label="'
                out << attrs.owner[attrs.field]
                out << '" href="'
                out << url
                out << '" target="_blank"><i class="external alternate icon"></i></a>'
            }
        }
    }

    def xEditableRefData = { attrs, body ->

        User user = springSecurityService.currentUser
        boolean isAdmin = user?.hasRole("ROLE_ADMIN")

        boolean editable = isEditable(request.getAttribute('editable'), attrs.overwriteEditable)

        if (editable) {

            def owner = ClassUtils.deproxy(attrs.remove("owner"))

            def config = attrs.remove("config")
            def data_link = createLink(controller: 'ajaxSupport', action: 'getRefdata', params: [id: config, format: 'json'])
            def update_link = createLink(controller: 'ajaxSupport', action: 'genericSetRel', params: [curationOverride: params.curationOverride])
            def oid = owner.id != null ? "${owner.class.name}:${owner.id}" : ''
            def id = attrs.remove("id") ?: "${oid}:${attrs.field}"
            //def type = attrs.remove("type") ?: "select"
            def field = attrs.remove("field")

            String default_empty = "Edit"
            String emptyText = attrs.emptytext ? " data-emptytext=\"${attrs.emptytext}\"" : " data-emptytext=\"${default_empty}\""

            out << "<span>"

            // Output an editable link
            out << "<span id=\"${id}\" "

            out << 'class="xEditableManyToOne"'

            if ((owner != null) && (owner.id != null)) {
                out << "data-pk=\"${oid}\" "
            }

            out << "data-url=\"${update_link}\" "

            if (attrs.required) {
                out << " data-required=\"${attrs.required}\" "
            }

            def attributes
           /* attributes = attrs.collect({ k, v ->

                if (v instanceof Collection) {
                    v = v.collect({ val ->
                        "${val}"
                    }).join(" ")
                }
                "${k}=\"${v.encodeAsHTML()}\""
            }).join(" ")*/
            out << "data-type=\"select\" data-name=\"${field}\" data-source=\"${data_link}\" "

            if(attributes)
            out << " ${attributes} "

            out << ">"

            // Here we can register different ways of presenting object references. The most pressing need to be
            // outputting a span containing an icon for refdata fields.
            out << renderObjectValue(owner[field])

            out << "</span>"

            // If the caller specified an rdc attribute then they are describing a refdata category.
            // We want to add a link to the category edit page IF the annotation is editable.

            if (isAdmin) {
                RefdataCategory rdc = RefdataCategory.findByDesc(config)
                if (rdc) {
                    out << '&nbsp;&nbsp;<a href="' + createLink(controller: 'resource', action: 'show', id: 'org.gokb.cred.RefdataCategory:' + rdc.id) + '"title="Jump to RefdataCategory"><i class="ui icon eye"></i></a><br/>'
                }
            }

            out << "</span>"
        } else {
            if (body) {
                out << body()
            } else {
                def content = (attrs.owner?."${attrs.field}" ? renderObjectValue(attrs.owner."${attrs.field}") : "")
                out << "${content ?: 'Empty'}"
            }
        }

    }

    def xEditableBoolean = { attrs, body ->

        /* User user = springSecurityService.currentUser
         boolean isAdmin = user.hasRole("ROLE_ADMIN")
         }
     */
        // The check editable should output the read only version so we should just exit
        // if read only.
        //if (!checkEditable(attrs, body, out)) return

        boolean editable = isEditable(request.getAttribute('editable'), attrs.overwriteEditable)

        if (editable) {

            def owner = ClassUtils.deproxy(attrs.remove("owner"))

            def data_link = createLink(controller: 'ajaxSupport', action: 'getRefdata', params: [id: 'boolean', format: 'json'])
            def update_link = createLink(controller: 'ajaxSupport', action: 'genericSetRel', params: [type: 'boolean', curationOverride: params.curationOverride])
            def oid = owner.id != null ? "${owner.class.name}:${owner.id}" : ''
            def id = attrs.remove("id") ?: "${oid}:${attrs.field}"
            def field = attrs.remove("field")
            //attrs['class'] = ["xEditableManyToOne"]

            String default_empty = "Edit"
            String emptyText = attrs.emptytext ? " data-emptytext=\"${attrs.emptytext}\"" : " data-emptytext=\"${default_empty}\""

            out << "<span>"

            // Output an editable link
            out << "<span id=\"${id}\" "

            out << 'class="xEditableManyToOne"'

            if ((owner != null) && (owner.id != null)) {
                out << "data-pk=\"${oid}\" "
            }

            out << "data-url=\"${update_link}\" "

            if (attrs.required) {
                out << " data-required=\"${attrs.required}\" "
            }

            def attributes

/*            attributes = attrs.collect({ k, v ->
                if (v instanceof Collection) {
                    v = v.collect({ val ->
                        "${val}"
                    }).join(" ")
                }
                "${k}=\"${v.encodeAsHTML()}\""
            }).join(" ")*/

            out << "data-type=\"select\" data-name=\"${field}\" data-source=\"${data_link}\" "

            if(attributes)
                out << " ${attributes} "

            out << ">"

            // Here we can register different ways of presenting object references. The most pressing need to be
            // outputting a span containing an icon for refdata fields.
            out << renderObjectValue(owner[field])

            out << "</span>"

            // If the caller specified an rdc attribute then they are describing a refdata category.
            // We want to add a link to the category edit page IF the annotation is editable.

            out << "</span>"
        } else {
            out << renderObjectValue(attrs.owner[attrs.field])
        }

    }

    def renderObjectValue(value) {
        def result = ''
        if (value != null) {
            switch (value.class) {
                case org.gokb.cred.RefdataValue.class:
                    result = value.getI10n('value')
                    break;
                case Boolean.class:
                    result = (value == true ? 'Yes' : 'No')
                    break;
                case Date.class:
                    def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd')
                    result = sdf.format(value)
                    break
                default:
                    result = value.toString();
            }
        }
        result
    }



/*    *//**
     * simpleReferenceTypedown - create a hidden input control that has the value fully.qualified.class:primary_key and which is editable with the
     * user typing into the box. Takes advantage of refdataFind and refdataCreate methods on the domain class.
     *//*
    def simpleReferenceTypedown = { attrs, body ->

        // The check editable should output the read only version so we should just exit
        // if read only.
        //if (!checkEditable(attrs, body, out)) return

        out << "<input type=\"hidden\" value=\"${attrs.value ?: ''}\" name=\"${attrs.name}\" data-domain=\"${attrs.baseClass}\" "
        if (attrs.id) {
            out << "id=\"${attrs.id}\" "
        }
        if (attrs.style) {
            out << "style=\"${attrs.style}\" "
        }

        if ((attrs.value != null) && (attrs.value.length() > 0)) {
            def o = genericOIDService.resolveOID2(attrs.value)
            out << "data-displayValue=\"${o.toString()}\" "
        }

        if (attrs.elastic) {
            out << "data-elastic=\"${attrs.elastic}\""
        }

        if (attrs.require) {
            out << "data-require=\"true\" "
        }

        if (attrs.filter1) {
            out << "data-filter1=\"${attrs.filter1}\" "
        }

        out << "class=\"simpleReferenceTypedown ${attrs.class}\" />"
    }*/

    def simpleReferenceDropdown = { attrs, body ->

        out << '<div class="ui fluid search selection dropdown simpleReferenceDropdown '

        if(attrs.class)
            out << attrs.class

        out << '"'

        if (attrs.style) {
            out << "style=\"${attrs.style}\" "
        }

        if (attrs.id) {
            out << "id=\"${attrs.id}\" "
        }
        out << "/>"

        out << "<input type=\"hidden\" value=\"${attrs.value ?: ''}\" name=\"${attrs.name}\" data-domain=\"${attrs.baseClass}\" "

        if ((attrs.value != null) && (attrs.value instanceof String && attrs.value.length() > 0)) {
            def o = genericOIDService.resolveOID2(attrs.value)
            out << "data-displayValue=\"${o.toString()}\" "
        }

        if (attrs.elastic) {
            out << "data-elastic=\"${attrs.elastic}\""
        }

/*        if (attrs.require) {
            out << "data-require=\"true\" "
        }*/

        if (attrs.filter1) {
            out << "data-filter1=\"${attrs.filter1}\" "
        }
        out << "/>"
        out << '<i class="remove icon"></i>'
        out << '<i class="dropdown icon"></i>'
        out << '<div class="default text">Search for...</div>'
        out << '<div class="menu"></div>'
        out << '</div>'

    }

    def xEditableManyToOne = { attrs, body ->

        boolean editable = isEditable(request.getAttribute('editable'), attrs.overwriteEditable)

        if (editable) {
            def owner = ClassUtils.deproxy(attrs.owner)

            def oid = attrs.owner.id != null ? "${owner.class.name}:${owner.id}" : ''
            def id = attrs.id ?: "${oid ?: owner.class.name}:${attrs.field}"
            def update_link = createLink(controller: 'ajaxSupport', action: 'genericSetRel', params: [curationOverride: params.curationOverride])

            String data_link = createLink(
                    controller:'ajaxJson',
                    action: 'lookup',
                    params: [baseClass: attrs.baseClass,
                            q: '',
                            filter1: attrs.filter1,
                            preparForEditable: 'true']
            ).encodeAsHTML()

            String default_empty = "Edit"
            String emptyText = attrs.emptytext ? " data-emptytext=\"${attrs.emptytext}\"" : " data-emptytext=\"${default_empty}\""

            def follow_link = null

            if (owner != null && owner[attrs.field] != null) {
                String urlWithClassAndID = null
                if(!(owner[attrs.field] instanceof org.gokb.cred.KBComponent))
                    urlWithClassAndID = "${ClassUtils.deproxy(owner[attrs.field]).class.name}" + ':' + owner[attrs.field].id

                follow_link = createLink(controller: 'resource', action: 'show', id: urlWithClassAndID ?: owner[attrs.field].uuid)
            }
                out << "<a href=\"#\" data-domain=\"${attrs.baseClass}\" id=\"${id}\" class=\"xEditableManyToOne\" "

                if ((attrs.filter1 != null) && (attrs.filter1.length() > 0)) {
                    out << "data-filter1=\"${attrs.filter1}\" "
                }

                if (owner?.id != null)
                    out << "data-pk=\"${oid}\" "

                if (attrs.required) {
                    out << " data-required=\"${attrs.required}\" "
                }

                if ((attrs.value != null) && (attrs.value instanceof String && attrs.value.length() > 0)) {
                    def o = genericOIDService.resolveOID2(attrs.value)
                    out << "data-value=\"${o.id.toString()}\" "
                }

                out << "data-type=\"select\" data-name=\"${attrs.field}\" data-source=\"${data_link}\" data-url=\"${update_link}\" ${emptyText}>"
                if (body) {
                    out << body()
                } else {
                    String content = (attrs.owner?."${attrs.field}" ? renderObjectValue(attrs.owner."${attrs.field}") : "")
                    if(content){
                        out << content
                    }else {
                        out << ""
                    }
                }
                out << "</a>"


            if(attrs.owner && attrs.owner."${attrs.field}")
                out << g.link('Unlink', controller: "ajaxSupport", action: "unlinkManyToOne", class: "ui right floated negative mini button", params: ['curationOverride': params.curationOverride, '__property': attrs.field, '__context': attrs.owner.getClassName() + ':' + attrs.owner.id])

          if (follow_link) {
                out << ' &nbsp; <a href="' + follow_link + '" title="Jump to resource"><i class="ui share square icon"></i></a>'
            }
        } else {
            if (body) {
                out << body()
            } else {
                String content = (attrs.owner?."${attrs.field}" ? renderObjectValue(attrs.owner."${attrs.field}") : "")
                if(content){
                    out << g.link(content, controller: 'resource', action: 'show', id: attrs.owner."${attrs.field}".uuid)
                }else {
                    out << "Empty"
                }
            }
        }
    }

    private boolean isEditable(editable, overwrite) {

        boolean result = Boolean.valueOf(editable)

        List positive = [true, 'true', 'True', 1]
        List negative = [false, 'false', 'False', 0]

        if (overwrite in positive) {
            result = true
        } else if (overwrite in negative) {
            result = false
        }

        result
    }


}
