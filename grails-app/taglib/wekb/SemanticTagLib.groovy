package wekb

import grails.util.TypeConvertingMap
import grails.web.mapping.UrlMapping
import org.grails.encoder.CodecLookup
import org.grails.encoder.Encoder
import org.grails.taglib.TagOutput
import org.grails.taglib.encoder.OutputContextLookupHelper
import org.springframework.context.MessageSource
import org.springframework.web.servlet.support.RequestContextUtils
import wekb.helper.BeanStore

class SemanticTagLib {

    CodecLookup codecLookup

    static namespace = 'semui'

    def actionsDropdown = { attrs, body ->

        out << '<div class="ui simple dropdown primary button">'
        out << '<div class="text">'
        out << attrs.text
        out << '</div>'
        out <<  '<i class="dropdown icon"></i>'
        out <<  '<div class="menu" style="left: auto; right: 0">'

        out <<          body()

        out <<  '</div>'
        out << '</div>'
    }

    def actionsDropdownItem = { attrs, body ->

        def text = attrs.text
        String linkBody  = text ?: ''
        String aClass    = attrs.class ? attrs.class + ' item' : 'item'
        String href      = attrs.href ? attrs.href : '#'

        if (attrs.tooltip && attrs.tooltip != '') {
            linkBody = '<div class="" data-content="' + attrs.tooltip +'">' + linkBody + '</div>'
        }
        if (this.pageScope.variables?.actionName == attrs.action && !attrs.notActive) {
            aClass = aClass + ' active'
        }
        if (attrs.icon && attrs.icon != '') {
            linkBody += '<i class="' + attrs.icon +' icon"></i>'
        }

        if (attrs.description && attrs.description != '') {
            linkBody += '<span class="description">' + attrs.description +'</span>'
        }

        def linkParams = [
                class: aClass,
                controller: attrs.controller,
                action: attrs.action,
                params: attrs.params
        ]
        if (attrs.onclick) {
            linkParams.onclick = attrs.onclick
        }

        if (attrs.controller) {
            out << g.link(linkParams, linkBody)
        }
        else {
            out << '<a href="' + href + '" class="item"'
            if (attrs.id) { // e.g. binding js events
                out << ' id="' + attrs.id + '">'
            }
            if (attrs.'data-semui') { // e.g. binding modals
                out << ' data-semui="' + attrs.'data-semui' + '">'
            }
            out << linkBody + '</a>'
        }
    }

    Closure breadcrumbs = { attrs, body ->

        out <<   '<div class="ui breadcrumb">'
        out <<      breadcrumb([controller: 'home', text:'<i class="home icon"></i>'])
        out <<          body()
        out <<   '</div>'
    }

    Closure breadcrumb = { attrs, body ->

        String linkBody  = attrs.text ?: ''

        if (attrs.controller) {
            if (attrs.controller != 'home') {
                linkBody = linkBody.encodeAsHTML()
            }

            out << g.link(
                    linkBody,
                    controller: attrs.controller,
                    action: attrs.action,
                    params: attrs.params,
                    class: 'section' + (attrs.class ? " ${attrs.class}" : ''),
                    id: attrs.id
            )
            if (! "active".equalsIgnoreCase(attrs.class.toString())) {
                out << '<i class="right angle icon divider"></i>'
            }
        }
        else {
            out << '<a class="active section">' << linkBody.encodeAsHTML() << '</a>'
        }

    }

    Closure card = { attrs, body ->
        String title = attrs.text ?: ''

        out << '<div class="ui card ' + attrs.class + '">'
        out << '    <div class="content">'

        if (title) {
            out << '    <div class="header">'
            out << '        <div class="ui grid">'
            out << '            <div class="twelve wide column">'
            out <<                title
            out << '            </div>'
            if (attrs.editable && attrs.href) {
                out << '        <div class="right aligned four wide column">'
                out << '            <button type="button" class="ui icon button" data-semui="modal" data-href="' + attrs.href + '" ><i aria-hidden="true" class="plus icon"></i></button>'
                out << '        </div>'
            }
            out << '        </div>'
            out << '   </div>'

        }
        out << body()

        out << '    </div>'
        out << '</div>'
    }

    Closure flashMessage = { attrs, body ->

        def flash = attrs.data

        if (flash && flash.message) {
            out << '<div class="ui message">'
            out << '<i class="close icon"></i>'
            out << '<p>'
            if(flash.message instanceof Map || flash.message instanceof List){
                generateSemuiList(flash.message)
            }
            else{
                out << flash.message
            }
            out << '</p>'
            out << '</div>'
        }

        if (flash && flash.info) {
            out << '<div class="ui message">'
            out << '<i class="close icon"></i>'
            out << '<p>'
            if(flash.info instanceof Map || flash.info instanceof List){
                generateSemuiList(flash.info)
            }
            else{
                out << flash.info
            }
            out << '</p>'
            out << '</div>'
        }

        if (flash && flash.success) {
            out << '<div class="ui success message">'
            out << '<i class="close icon"></i>'
            out << '<p>'
            if(flash.success instanceof Map || flash.success instanceof List){
                generateSemuiList(flash.success)
            }
            else{
                out << flash.success
            }
            out << '</p>'
            out << '</div>'
        }

        if (flash && flash.error) {
            out << '<div class="ui negative message">'
            out << '<i class="close icon"></i>'
            out << '<p>'
            if(flash.error instanceof Map || flash.error instanceof List){
                generateSemuiList(flash.error)
            }
            else{
                out << flash.error
            }
            out << '</p>'
            out << '</div>'
        }
    }

    Closure message = { attrs, body ->

        def message = attrs.message

        if (message || body()) {
            out << '<div class="ui '
            if(attrs.class)
                out << attrs.class
            out << ' message">'
            out << '<i class="close icon"></i>'
            if(message) {
                out << '<p>'
                out << message
                out << '</p>'
            }else{
                out << body()
            }
            out << '</div>'
        }
    }

    Closure modal = { attrs, body ->

        String id           = attrs.id ? ' id="' + attrs.id + '" ' : ''
        String modalSize    = attrs.modalSize ? attrs.modalSize  : ''
        String title        = attrs.title
        String isEditModal  = attrs.isEditModal

        String msgClose    = attrs.msgClose  ?: "Close"
        String msgSave     = attrs.msgSave   ?: (isEditModal ? "Save" : "Add")
        String msgDelete   = attrs.msgDelete ?: "${g.message(code:'default.button.delete.label')}"

        out << '<div role="dialog" class="ui large modal ' + modalSize + '"' + id + ' aria-label="Modal">'
        out << '<div class="header">' + title + '</div>'

        if (attrs.contentClass) {
            out << '<div class="content ' + attrs.contentClass + '">'
        } else {
            out << '<div class="content">'
        }

        out << body()
        out << '</div>'
        out << '<div class="actions">'
        out << '<button class="ui button ' + attrs.id + '" onclick="$(\'#' + attrs.id + '\').modal(\'hide\')">' + msgClose + '</button>'

        if (attrs.showDeleteButton) {

            out << '<input type="submit" class="ui negative button" name="delete" value="' + msgDelete + '" onclick="'
            out << "return confirm('Do you really want to delete this?')?"
            out << '$(\'#' + attrs.id + '\').find(\'#' + attrs.deleteFormID + '\').submit():null'
            out << '"/>'
        }

        if (attrs.hideSubmitButton == null) {
            if (attrs.formID) {
                out << '<input type="submit" class="ui button positive" name="save" value="' + msgSave + '" onclick="event.preventDefault(); $(\'#' + attrs.id + '\').find(\'#' + attrs.formID + '\').submit()"/>'
            } else {
                out << '<input type="submit" class="ui button positive" name="save" value="' + msgSave + '" onclick="event.preventDefault(); $(\'#' + attrs.id + '\').find(\'form\').submit()"/>'
            }
        }

        out << '</div>'
        out << '</div>'
    }

    Closure paginate = { Map attrsMap ->
        TypeConvertingMap attrs = (TypeConvertingMap)attrsMap
        def writer = out
        if (attrs.total == null) {
            throwTagError("Tag [paginate] is missing required attribute [total]")
        }

        def messageSource = grailsAttributes.messageSource
        def locale = RequestContextUtils.getLocale(request)

        def total = attrs.int('total') ?: 0
        def offset = params.int('offset') ?: 0
        def max = params.int('max')
        def maxsteps = (attrs.int('maxsteps') ?: 10)

        if (!offset) offset = (attrs.int('offset') ?: 0)
        if (!max) max = (attrs.int('max') ?: 10)

        if (total <= max) {
            return
        }

        Map linkParams = [:]
        if (attrs.params instanceof Map) linkParams.putAll((Map)attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        Map linkTagAttrs = [:]
        def action
        if (attrs.containsKey('mapping')) {
            linkTagAttrs.mapping = attrs.mapping
            action = attrs.action
        } else {
            action = attrs.action ?: params.action
        }
        if (action) {
            linkTagAttrs.action = action
        }
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.containsKey(UrlMapping.PLUGIN)) {
            linkTagAttrs.put(UrlMapping.PLUGIN, attrs.get(UrlMapping.PLUGIN))
        }
        if (attrs.containsKey(UrlMapping.NAMESPACE)) {
            linkTagAttrs.put(UrlMapping.NAMESPACE, attrs.get(UrlMapping.NAMESPACE))
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.tab != null) {
            linkTagAttrs.tab = attrs.tab
        }
        linkTagAttrs.params = linkParams

        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = ((offset / max) as int) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil((total / max) as double)) as int


        out << '<!--.pagination-->'
        out << '<div class="ui center aligned basic segment">'
        out << '<nav class="ui pagination wrapping menu" aria-label="pagination2">'

        if (currentstep > firststep) {
            int tmp = (offset - (max * (maxsteps +1)))
            linkParams.offset = tmp > 0 ? tmp : 0
            linkTagAttrs.class = (currentstep == firststep) ? "item disabled prevLink" : "item prevLink"

            def prevLinkAttrs1 = linkTagAttrs.clone()
            prevLinkAttrs1.title = "Previous ${linkParams.offset} results"
            out << link((prevLinkAttrs1), '<i class="double angle left icon"></i>')

            // | < |
            linkParams.offset = offset - max
            linkTagAttrs.class = (currentstep == firststep) ? "item disabled prevLink" : "item prevLink"

            def prevLinkAttrs2 = linkTagAttrs.clone()
            prevLinkAttrs2.title = "First results"
            out << link((prevLinkAttrs2), '<i class="angle left icon"></i>')
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.put('class', 'item')

            // determine begin and endstep paging variables
            int beginstep = currentstep - (Math.round(maxsteps / 2.0d) as int) + (maxsteps % 2)
            int endstep = currentstep + (Math.round(maxsteps / 2.0d) as int) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkParams.offset = 0
                writer << callLink((Map)linkTagAttrs.clone()) {firststep.toString()}
            }

            if (beginstep > firststep+1) {
                writer << '<div class="disabled item">...</div>'
            }

            // display paginate steps
            (beginstep..endstep).each { int i ->
                if (currentstep == i) {
                    writer << "<a class=\"active item\">${i}</a>"
                }
                else {
                    linkParams.offset = (i - 1) * max
                    writer << callLink((Map)linkTagAttrs.clone()) {i.toString()}
                }
            }

            if (endstep+1 < laststep) {
                writer << '<div class="disabled item">...</div>'
            }
            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                linkParams.offset = (laststep - 1) * max
                writer << callLink((Map)linkTagAttrs.clone()) { laststep.toString() }
            }
        }


        if (currentstep < laststep) {
            linkParams.offset = offset + max
            linkTagAttrs.class = (currentstep == laststep) ? "item disabled nextLink" : "item nextLink"

            def nextLinkAttrs1 = linkTagAttrs.clone()
            nextLinkAttrs1.title = "Next ${linkParams.offset} results"
            out << link((nextLinkAttrs1), '<i class="angle right icon"></i>')
            if (currentstep < laststep-maxsteps-1) {
                int tmp = linkParams.offset + (max * maxsteps)
                linkParams.offset = tmp < total ? tmp : ((laststep - 1) * max)
                linkTagAttrs.class = (currentstep == laststep) ? "item disabled nextLink" : "item nextLink"

                def nextLinkAttrs2 = linkTagAttrs.clone()
                nextLinkAttrs2.title = "Last results"
                out << link((nextLinkAttrs2), '<i class="double angle right icon"></i>')
            }
        }

        out << '</nav>'
        out << '</div><!--.pagination-->'
    }

    def paginateNew = { attrs ->

        if (attrs.total == null) {
            log.debug("throwTagError(\"Tag [paginate] is missing required attribute [total]\")")
        }

        MessageSource messageSource = BeanStore.getMessageSource()
        Locale locale = RequestContextUtils.getLocale(request)

        String action = (attrs.action ? attrs.action : (params.action ? params.action : "list"))

        def total = attrs.int('total') ?: 0
        def offset = params.int('offset') ?: 0
        def max = params.int('max')
        def maxsteps = (attrs.int('maxsteps') ?: 10)

        if (!offset) offset = (attrs.int('offset') ?: 0)
        if (!max) max = (attrs.int('max') ?: 10)

        if (total <= max) {
            return
        }

        Map linkParams = [:]
        if (attrs.params instanceof Map) linkParams.putAll((Map)attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        Map linkTagAttrs = [:]
        if (attrs.containsKey('mapping')) {
            linkTagAttrs.mapping = attrs.mapping
            action = attrs.action
        } else {
            action = attrs.action ?: params.action
        }
        if (action) {
            linkTagAttrs.action = action
        }
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.containsKey(UrlMapping.PLUGIN)) {
            linkTagAttrs.put(UrlMapping.PLUGIN, attrs.get(UrlMapping.PLUGIN))
        }
        if (attrs.containsKey(UrlMapping.NAMESPACE)) {
            linkTagAttrs.put(UrlMapping.NAMESPACE, attrs.get(UrlMapping.NAMESPACE))
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.tab != null) {
            linkTagAttrs.tab = attrs.tab
        }
        linkTagAttrs.params = linkParams

        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = ((offset / max) as int) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil((total / max) as double)) as int

        Map prevMap = [title: (attrs.prev ?: messageSource.getMessage('default.paginate.prev', null, null, locale))]
        Map nextMap = [title: (attrs.next ?: messageSource.getMessage('default.paginate.next', null, null, locale))]


        out << '<div class="ui center aligned basic segment">'
        out << '<nav class="ui pagination wrapping menu">'

        if (steps && laststep > firststep) {
            if (maxsteps > laststep) { // | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | > |
                // prev-buttons
                if (currentstep > firststep) {
                    // <
                    linkParams.offset = offset - max
                    linkTagAttrs.class = (currentstep == firststep) ? "item disabled prevLink" : "item prevLink"

                    def prevLinkAttrs2 = linkTagAttrs.clone()
                    out << callLink((prevLinkAttrs2 += prevMap), '<i class="angle left icon"></i>')
                }

                if (currentstep != 1) {
                    def fristLinkAttrs = linkTagAttrs.clone()
                    fristLinkAttrs.params.offset = 0
                    fristLinkAttrs.class = 'item '
                    out << callLink(fristLinkAttrs, "1")

                    // | ... |
                    out << '  <div class="disabled item">\n' +
                            '    ...\n' +
                            '  </div>'
                }

                // steps
                for (int i in currentstep..(currentstep + maxsteps)) {
                    if (((i - 1) * max) < total) {
                        linkParams.offset = (i - 1) * max
                        if (currentstep == i) {
                            linkTagAttrs.class = "item active"
                        } else {
                            linkTagAttrs.class = "item"
                        }
                        out << callLink(linkTagAttrs.clone()) { i.toString() }
                    }
                }
                // next-buttons
                if (currentstep < laststep) {
                    // <
                    linkParams.offset = offset + max
                    linkTagAttrs.class = (currentstep == laststep) ? "item disabled nextLink" : "item nextLink"

                    def nextLinkAttrs1 = linkTagAttrs.clone()
                    out << callLink((nextLinkAttrs1 += nextMap), '<i class="angle right icon"></i>')
                }
            }
            else { // | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | ... | 8121 | > | >> |

                // | << | < |
                if (currentstep > firststep) {
                    // | << |
                    int tmp = (offset - (max * (maxsteps +1)))
                    linkParams.offset = tmp > 0 ? tmp : 0
                    linkTagAttrs.class = (currentstep == firststep) ? "item disabled prevLink" : "item prevLink"

                    def prevLinkAttrs1 = linkTagAttrs.clone()
                    Map prevMap2 = [title: (attrs.prev ?: " ${maxsteps} " + messageSource.getMessage('default.paginate.prev', null, null, locale))]
                    out << callLink((prevLinkAttrs1 += prevMap2), '<i class="double angle left icon"></i>')

                    // | < |
                    linkParams.offset = offset - max
                    linkTagAttrs.class = (currentstep == firststep) ? "item disabled prevLink" : "item prevLink"

                    def prevLinkAttrs2 = linkTagAttrs.clone()
                    out << callLink((prevLinkAttrs2 += prevMap), '<i class="angle left icon"></i>')
                }

                if (currentstep != 1) {
                    def fristLinkAttrs = linkTagAttrs.clone()
                    fristLinkAttrs.params.offset = 0
                    fristLinkAttrs.class = 'item '
                    out << callLink(fristLinkAttrs, "1")

                    // | ... |
                    out << '  <div class="disabled item">\n' +
                            '    ...\n' +
                            '  </div>'
                }


                // steps | 1 | 2 | 3 | 4 |
                for (int i in currentstep..(currentstep + maxsteps)) {
                    if (((i) * max) < total) {
                        linkParams.offset = (i - 1) * max
                        if (currentstep == i) {
                            linkTagAttrs.class = "item active"
                        } else {
                            linkTagAttrs.class = "item"
                        }
                        out << callLink(linkTagAttrs.clone()) { i.toString() }
                    }
                }
                // | ... |
                if (currentstep < laststep-maxsteps-1) {
                    out << '  <div class="disabled item">\n' +
                            '    ...\n' +
                            '  </div>'
                }
                // laststep | 1154 |
                if (currentstep == laststep) {
                    linkTagAttrs.class = "item active"
                } else {
                    linkTagAttrs.class = "item"
                }
                def lastLinkAttrs = linkTagAttrs.clone()
                linkParams.offset = (laststep-1) * max
                out << callLink(lastLinkAttrs) {laststep.toString() }

                // | > | >> |
                if (currentstep < laststep) {
                    // | > |
                    linkParams.offset = offset + max
                    linkTagAttrs.class = (currentstep == laststep) ? "item disabled nextLink" : "item nextLink"

                    def nextLinkAttrs1 = linkTagAttrs.clone()
                    out << callLink((nextLinkAttrs1 += nextMap), '<i class="angle right icon"></i>')
                    if (currentstep < laststep-maxsteps-1) {
                        // | >> |
                        int tmp
                        tmp = linkParams.offset + (max * maxsteps)
                        linkParams.offset = tmp < total ? tmp : ((laststep - 1) * max)
                        linkTagAttrs.class = (currentstep == laststep) ? "item disabled nextLink" : "item nextLink"

                        def nextLinkAttrs2 = linkTagAttrs.clone()
                        Map nextMap2 = [title: (attrs.next ?: messageSource.getMessage('default.paginate.next', null, null, locale)) +" ${maxsteps} "]
                        out << callLink((nextLinkAttrs2 += nextMap2), '<i class="double angle right icon"></i>')
                    }
                }
            }
        }
        // all button
        Map<String, Object> allLinkAttrs = linkTagAttrs.clone()
        Map<String, Object> customInputAttrs = linkTagAttrs.clone()
        customInputAttrs.params = new LinkedHashMap<String, Object>()
        customInputAttrs.params.putAll(linkTagAttrs.params)
        allLinkAttrs.putAt('title', messageSource.getMessage('default.paginate.all', null, locale))
        if (total <= 200) {
            allLinkAttrs.class = "item"
            allLinkAttrs.params.remove('offset')
            allLinkAttrs.params.max = 200
            out << callLink(allLinkAttrs, '<i class="list icon"></i>')
        }
        else {
            out << '<div class="disabled item wekb popup" data-content="'+messageSource.getMessage('default.paginate.listTooLong',null,locale)+'"><i class="list icon"></i></div>'
        }

        // Custom Input

        out << '<div class="item wekb-pagination-custom-input" data-max="' + max + '" data-steps="' + laststep + '">'
        out << '    <div class="ui mini form">'
        out << '            <div class="field">'
        out << '                <input autocomplete="off" data-validate="pagination-custom-validate" maxlength="6" aria-label="' + messageSource.getMessage('pagination.keyboardInput.placeholder',null,locale) + '" placeholder="' + messageSource.getMessage('pagination.keyboardInput.placeholder',null,locale) + '" type="text">'
        customInputAttrs.params.remove('offset')
        customInputAttrs.params.remove('class')
        customInputAttrs.class= "wekb-pagination-custom-link js-no-wait-wheel"
        customInputAttrs['aria-label']= messageSource.getMessage('pagination.keyboardInput.goToPage',null,locale)
        customInputAttrs
        out << callLink(customInputAttrs, '<i class="large chevron circle right icon wekb popup" data-content="' + messageSource.getMessage('pagination.keyboardInput.goToPage',null,locale) + '"></i>')
        out << '            </div>'
        out << '    </div>'
        out << '</div>'
        out << '</nav>'
        out << '</div><!--.pagination-->'
    }

    Closure showOutGoingLink = { Map attrs ->
        if (attrs.outGoingLink) {
            String url = attrs.outGoingLink.startsWith('http') ? attrs.outGoingLink : ('http://' + attrs.outGoingLink)
            if (url) {
                out << '&nbsp;<a aria-label="'
                out << attrs.text
                out << '" href="'
                out << url
                out << '" target="_blank"><i class="external alternate icon"></i></a>'
            }
        }
    }

    Closure sortableColumn = { Map attrs ->
        def writer = out
        if (!attrs.property) {
            throwTagError("Tag [sortableColumn] is missing required attribute [property]")
        }

        if (!attrs.title && !attrs.titleKey) {
            throwTagError("Tag [sortableColumn] is missing required attribute [title] or [titleKey]")
        }

        def property = attrs.remove("property")
        def action = attrs.action ? attrs.remove("action") : (actionName ?: "list")
        def controller = attrs.controller ? attrs.remove("controller") : (controllerName ?: "")
        def namespace = attrs.namespace ? attrs.remove("namespace") : ""

        def defaultOrder = attrs.remove("defaultOrder")
        if (defaultOrder != "desc") defaultOrder = "asc"

        // current sorting property and order
        def sort = params.sort
        def order = params.order

        // add sorting property and params to link params
        Map linkParams = [:]
        if (params.id) linkParams.put("id", params.id)
        def paramsAttr = attrs.remove("params")
        if (paramsAttr instanceof Map) linkParams.putAll(paramsAttr)
        linkParams.sort = property

        // propagate "max" and "offset" standard params
        if (params.max) linkParams.max = params.max
        if (params.offset) linkParams.offset = params.offset

        // determine and add sorting order for this column to link params
        //attrs['class'] = (attrs['class'] ? "${attrs['class']} sortable" : "sortable")
        if (property == sort) {
            if (order == "asc") {
                attrs['class'] = (attrs['class'] as String) + " sorted ascending"
                linkParams.order = "desc"
            }
            else {
                attrs['class'] = (attrs['class'] as String) + " sorted descending"
                linkParams.order = "asc"
            }
        }
        else {
            linkParams.order = property == 'lastUpdated' ? "desc" : defaultOrder
        }

        // determine column title
        String title = attrs.remove("title") as String
        String titleKey = attrs.remove("titleKey") as String
        Object mapping = attrs.remove('mapping')
        if (titleKey) {
            if (!title) title = titleKey
            def messageSource = grailsAttributes.messageSource
            def locale = RequestContextUtils.getLocale(request)
            title = messageSource.getMessage(titleKey, null, title, locale)
        }

        writer << "<th "
        // process remaining attributes
        Encoder htmlEncoder = codecLookup.lookupEncoder('HTML')
        attrs.each { k, v ->
            writer << k
            writer << "=\""
            writer << htmlEncoder.encode(v)
            writer << "\" "
        }
        writer << '>'
        Map linkAttrs = [:]
        linkAttrs.params = linkParams
        if (mapping) {
            linkAttrs.mapping = mapping
        }

        linkAttrs.action = action
        linkAttrs.controller = controller
        linkAttrs.namespace = namespace

        writer << callLink((Map)linkAttrs) {
            title
        }
        writer << '</th>'
    }

    Closure tabs = { attrs, body ->
        def newClass = attrs.class ?: ''
        out << '<div class="ui top attached tabular  ' + newClass + ' stackable menu">'
        out << body()
        out << '</div>'
    }

    Closure tabsItemWithLink = { attrs, body ->

        String linkBody = attrs.text ?: ''

        String aClass = ((this.pageScope.variables?.actionName == attrs.action && ((attrs.activeTab && attrs.activeTab == params.activeTab) || (attrs.tab && attrs.tab == params[attrs.subTab]))) ? 'item active' : 'item') + (attrs.class ? ' ' + attrs.class : '')

        String counts = (attrs.counts >= 0) ? '<div class="ui '  + ' circular primary label">' + attrs.counts + '</div>' : null

        linkBody = counts ? linkBody + counts : linkBody

        if (attrs.controller) {
            out << g.link(linkBody,
                    class: aClass,
                    controller: attrs.controller,
                    action: attrs.action,
                    params: attrs.params
            )
        } else {
            out << linkBody
        }
    }

    Closure tabsItemWithoutLink = { attrs, body ->

        out << '<div class="item '

        if(attrs.defaultTab && attrs.tab == attrs.defaultTab && !attrs.activeTab){
            out << 'active '
        }
        else if(attrs.activeTab && attrs.tab == attrs.activeTab){
            out << 'active '
        }

        out << (attrs.class ? (' ' + attrs.class) : '') +'" data-tab="' + attrs.tab + '">'
        out << body()

        if (attrs.counts != null) {
            out << '<div class="ui floating primary circular label">'+attrs.counts+'</div>'
        }
        out << '</div>'
    }

    Closure tabsItemContent = { attrs, body ->

        out << '<div class="ui bottom attached '

        if(attrs.defaultTab && attrs.tab == attrs.defaultTab && !attrs.activeTab){
            out << 'active '
        }
        else if(attrs.activeTab && attrs.tab == attrs.activeTab){
            out << 'active '
        }

        out << (attrs.class ? (' ' + attrs.class) : '') +' tab segment"' +' data-tab="' + attrs.tab + '">'
        out << body()
        out << '</div>'
    }

    private callLink(Map attrs, Object body) {
        TagOutput.captureTagOutput(tagLibraryLookup, 'g', 'link', attrs, body, OutputContextLookupHelper.lookupOutputContext())
    }

    private generateSemuiList(def data) {
        out << '<div class="ui bulleted list">'

        if (data instanceof Map) {
            data.each { Map map ->
                out << '<div class="ui bulleted list">'
                map.each {
                    out << '<div class="item">'
                    out << it
                    out << '</div>'
                }
                out << '</div>'
            }
        }

        if (data instanceof List) {

            data.each { def listItems ->
                if(listItems instanceof List){
                    listItems.each {
                        out << '<div class="item">'
                        out << it
                        out << '</div>'
                    }
                }else {
                    out << '<div class="item">'
                    out << listItems
                    out << '</div>'
                }

            }

        }

        out << '</div>'
    }
}
