package wekb

class BootStrap {

    BootStrapService bootStrapService

    def init = { servletContext ->
        log.info("------------------------------------Init Begin--------------------------------------------")
        bootStrapService.init()
        log.info("------------------------------------Init End--------------------------------------------")
    }




}
