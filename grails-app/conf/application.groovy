

println("-- using application.groovy config file !!! --")

grails.gorm.default.mapping = {
    autowire true
}

grails.gorm.default.constraints = {
    '*'(nullable: true, blank:false)
}

grails.gorm.autoFlush=true

//grails.gorm.failOnError=true

grails {
    plugin {
        auditLog {
            auditDomainClassName = "wekb.audit.AuditLogEvent"
            logFullClassName = false
        }
    }
}

// database migration plugin
grails.plugin.databasemigration.updateOnStart = true


// spring Security Core plugin
grails.plugin.springsecurity.successHandler.useReferer = true
grails.plugin.springsecurity.successHandler.alwaysUseDefault= false

grails.plugin.springsecurity.userLookup.userDomainClassName = 'wekb.auth.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'wekb.auth.UserRole'

grails.plugin.springsecurity.authority.className = 'wekb.auth.Role'

grails.plugin.springsecurity.ui.forgotPassword.emailFrom = "laser@hbz-nrw.de"
grails.plugin.springsecurity.ui.forgotPassword.emailSubject = "we:kb Forgotten Password"

grails.plugin.springsecurity.ui.password.minLength = 6
grails.plugin.springsecurity.ui.password.maxLength = 64
grails.plugin.springsecurity.ui.password.validationRegex = '^.*$'
/**
 * We need to disable springs password encoding as we handle this in our domain model.
 */
grails.plugin.springsecurity.ui.encodePassword = false

// The following 2 entries make the app use basic auth by default
grails.plugin.springsecurity.useBasicAuth = true

grails.plugin.springsecurity.basic.realmName = "wekb"

grails.plugin.springsecurity.roleHierarchy = '''
    ROLE_YODA > ROLE_ADMIN
    ROLE_ADMIN > ROLE_USER
'''

// TODO: 2023 check filterChain and controllerAnnotations
grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/login/auth',          filters: 'none'],
        [pattern: '/assets/**',           filters: 'none'],
        [pattern: '/**/js/**',            filters: 'none'],
        [pattern: '/**/css/**',           filters: 'none'],
        [pattern: '/**/images/**',        filters: 'none'],
        [pattern: '/**/favicon.ico',      filters: 'none'],
        [pattern: '/error',               filters: 'none'],
        [pattern: '/ajaxSupport/**',      filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/fwk/**',              filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/api/**',              filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/**',                  filters: 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter,-restTokenValidationFilter,-restExceptionTranslationFilter'],
]

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/admin/**',                access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/file/**',                 access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/info',                    access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/monitoring/**',           access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/login/auth',              access: ['permitAll']],
        [pattern: '/',                        access: ['permitAll']],
        [pattern: '/index',                   access: ['permitAll']],
        [pattern: '/notFound',                access: ['permitAll']],
        [pattern: '/index.gsp',               access: ['permitAll']],
        [pattern: '/public/**',               access: ['permitAll']],
        [pattern: '/static/**',               access: ['permitAll']],
        [pattern: '/packages/**',             access: ['permitAll']],
        [pattern: '/public',                  access: ['permitAll']],
        [pattern: '/error',                   access: ['permitAll']],
        [pattern: '/error/**',                access: ['permitAll']],
        [pattern: '/home/**',                 access: ['ROLE_USER']],
        [pattern: '/assets/**',               access: ['permitAll']],
        [pattern: '/**/js/**',                access: ['permitAll']],
        [pattern: '/**/css/**',               access: ['permitAll']],
        [pattern: '/**/images/**',            access: ['permitAll']],
        [pattern: '/**/favicon.ico',          access: ['permitAll']],
        [pattern: '/integration/**',          access: ['permitAll']],
        [pattern: '/fwk/**',                  access: ['ROLE_USER']],
        [pattern: '/user/**',                 access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/user/search',             access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/user/edit/**',            access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/role/**',                 access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
        [pattern: '/securityInfo/**',         access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
        //NEW
        [pattern: '/search/**',          access: ['permitAll']],
        [pattern: '/resource/**',        access: ['permitAll']],
        [pattern: '/ajaxJson/**',    access: ['permitAll']],
        [pattern: '/package/**',        access: ['permitAll']],
        [pattern: '/api/index',                access: ['permitAll']],
        [pattern: '/api/find',                access: ['permitAll']],
        [pattern: '/api/scroll',              access: ['permitAll']],
        [pattern: '/api/sushiSources',        access: ['permitAll']],
        [pattern: '/api/suggest',             access: ['permitAll']],
        [pattern: '/api/isUp',                access: ['permitAll']],
        [pattern: '/api/namespaces',          access: ['permitAll']],
        [pattern: '/api/groups',              access: ['permitAll']]
]

//--------------------------------------------------------------------------------------------------------------------

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// whether to disable processing of multi part requests
grails.web.disable.multipart=false

grails.converters.json.circular.reference.behaviour = 'INSERT_NULL'

cache.headers.presets = [
        "none": false,
        "until_changed": [shared:true, validFor: (3600 * 12)] // cache content for 12 hours.
]

quartz.autoStartup = true
quartz.waitForJobsToCompleteOnShutdown = false


apiClasses = [
        "wekb.GrailsDomainHelpersApi"
]

wekb.es.globalSearch = [
        'indices'     : ['wekbtipps', 'wekborgs', 'wekbpackages', 'wekbplatforms'],
        'types'       : 'component',
        'typingField' : 'componentType',
        'port'        : 9300
]

wekb.es.searchApi = [
        'path'        : '/',
        'indices'     : ['wekbtipps', 'wekborgs', 'wekbpackages', 'wekbplatforms', 'wekbdeletedcomponents'],
        'types'       : 'component',
        'typingField' : 'componentType',
        'port'        : 9200
]



wekb.languagesUrl = 'http://localhost:8070'
wekb.ftupdate_enabled = true
wekb.packageUpdate.enabled = false

wekb.es.cluster = 'gokbg3-test'
wekb.es.indices = [
        tipps             : 'wekbtipps',
        orgs              : 'wekborgs',
        packages          : 'wekbpackages',
        platforms         : 'wekbplatforms',
        deletedKBComponent: 'wekbdeletedcomponents']


