package wekb

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import wekb.helper.RDStore

@Transactional
class AutoUpdatePlatformsService {

    GrailsApplication grailsApplication

    void updatePlatformCounterSources() {
        String urlString = "${grailsApplication.config.getProperty('counterRegistryUrl', String)}${grailsApplication.config.getProperty('counterRegistryDataSuffix', String)}"
        HttpClient baseClient = null
        try {
            baseClient = HttpClient.create(urlString.toURL())
            HttpRequest request = HttpRequest.GET(urlString.toURI())
            request.header('Accept', 'application/json,application/javascript,text/javascript')
            HttpResponse response = baseClient.toBlocking().exchange(request, String)
            int statusCode = response?.getStatus()?.getCode()
            log.debug('fetch successful? status: '+statusCode)
            if(statusCode >= 200 && statusCode < 300) {
                List allCounterPlatforms = []
                JSONArray allCounterPlatformsJSON = JSON.parse(response.body() as String) as JSONArray
                allCounterPlatforms.addAll(allCounterPlatformsJSON)
                allCounterPlatforms.each { Map platformRecord ->
                    String revision = null
                    Map counterStubRecord = platformRecord.sushi_services.find { Map counterStub -> counterStub.counter_release == '5.1' }
                    if (counterStubRecord) {
                        revision = '5.1'
                    }
                    else {
                        counterStubRecord = platformRecord.sushi_services.find { Map counterStub -> counterStub.counter_release == '5' }
                        if(counterStubRecord)
                            revision = '5'
                    }
                    if (revision) {
                        HttpClient detailsClient = null
                        try {
                            detailsClient = HttpClient.create(counterStubRecord.url.toURL())
                            HttpRequest detailsRequest = HttpRequest.GET(counterStubRecord.url.toURI())
                            detailsRequest.header('Accept', 'application/json,application/javascript,text/javascript')
                            HttpResponse detailsResponse = detailsClient.toBlocking().exchange(detailsRequest, String)
                            JSONObject platformDetailsRecord = JSON.parse(detailsResponse.body() as String) as JSONObject
                            Set<Platform> platforms
                            //log.debug(platformRecord.toMapString())
                            platforms = Platform.findAllByCounterRegistryApiUuid(platformRecord.id)
                            if(!platforms) {
                                platforms = Platform.findAllByPrimaryUrl(platformRecord.website)
                                /*if(!platforms) {
                                    platforms = Platform.findAllByCounterR5CounterServerUrl(platformDetailsRecord.url)
                                    if(platforms) {
                                        if(!(platformRecord.id in platforms.counterRegistryApiUuid))
                                            log.debug("UUID mismatch: ${platformRecord.id} not in ${platforms.counterRegistryApiUuid.toListString()}, located by ${platformDetailsRecord.url}")
                                        log.debug("platforms located by COUNTER server URL")
                                    }
                                }
                                else {*/
                                /*if(platforms) {
                                    log.debug("platforms located by primary URL")
                                }*/
                            }
                            //else log.debug("platforms located by UUID")
                            if(platformDetailsRecord) {
                                platforms.each { Platform platform ->
                                    updateRecord(platform, platformRecord.id, platformDetailsRecord) //! note: platformDetailsRecord.id != platformRecord.id !!!!!!!
                                }
                            }
                        }
                        catch (Exception e) {
                            log.error("error on details call: ${e.getMessage()}")
                        }
                        finally {
                            detailsClient.close()
                        }
                    }
                }
            }
            else {
                log.error("error on call at ${urlString}")
            }
        }
        catch (Exception e) {
            log.error(e.message)
        }
        finally {
            baseClient.close()
        }
    }

    void updateRecord(Platform platform, String registryUUID, Map platformRecord) {
        if(platform.counterRegistryApiUuid) {
            platform.counterR5CounterServerUrl = platformRecord.url
        }
        else if(!platform.counterRegistryApiUuid)
            platform.counterRegistryApiUuid = registryUUID
        boolean withRequestorId = Boolean.valueOf(platformRecord.requestor_id_required),
        withApiKey = Boolean.valueOf(platformRecord.qpi_key_required),
        withIpWhiteListing = Boolean.valueOf(platformRecord.ip_address_authorization)
        if(withRequestorId) {
            if(withApiKey) {
                platform.counterApiAuthenticationMethod = RDStore.API_AUTH_CUSTOMER_REQUESTOR_API
            }
            else platform.counterApiAuthenticationMethod = RDStore.API_AUTH_CUSTOMER_REQUESTOR
        }
        else if(withApiKey) {
            platform.counterApiAuthenticationMethod = RDStore.API_AUTH_CUSTOMER_API
        }
        else if(withIpWhiteListing) {
            platform.counterApiAuthenticationMethod = RDStore.API_IP_WHITELISTING
        }
        platform.save()
    }
}
