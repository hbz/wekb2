package wekb

import javassist.NotFoundException
import org.springframework.security.access.AccessDeniedException

class UrlMappings {

    static mappings = {
        "/"(controller: 'public', action: 'index')

        "/resource/show/$type/$id"(controller: 'resource', action: 'show')

        "/package"(controller: 'packages')

        // for no google indexing
        "/robots.txt"(controller: 'public', action: 'robots')


        // Server errors
        "400"(controller: 'error', action: 'badRequest')
        "401"(controller: 'error', action: 'unauthorized')
        "403"(controller: 'error', action: 'forbidden', params: params)
        "404"(controller: 'error', action: 'notFound')
        "405"(controller: 'error', action: 'wrongMethod')

        "500"(controller: 'error', action: 'forbidden', exception: NotFoundException)
        "500"(controller: 'error', action: 'unauthorized', exception: AccessDeniedException)
        "500"(controller: 'error', action: 'serverError')

        // default
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }
    }
}
