<g:set var="laserService" bean="${wekb.LaserService}"/>

<div class="ui warning icon message">

    <div class="content wekb-inline-lists">
        <div class="header">
            Package used in LAS:er: <g:link action="linkedSubsInLaser" controller="admin"
                                            id="${pkg.id}">${pkg.packageLinkedInLaserCount()}</g:link>

            <g:if test="${pkg.kbartSource}">
                <br>
                <br>
                <b><g:message code="kbartsource.kbartHasWekbFields"/>:</b> <semui:xEditableBoolean owner="${pkg.kbartSource}" field="kbartHasWekbFields"
                                                                                                   overwriteEditable="false"/> <i
                    class="info ${pkg.kbartSource.kbartHasWekbFields ? 'red' : 'green'} big circle icon"></i>
            </g:if>
        </div>

        <p>
            (PerpetualAccess: <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                      params="[perpetualAccess: true]">${laserService.packageLinkedWithPerpetualAccessInLaserCount(pkg.uuid)}</g:link> /
            Not PerpetualAccess: <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                         params="[perpetualAccess: false]">${laserService.packageLinkedWithOutPerpetualAccessInLaserCount(pkg.uuid)}</g:link>)

            <g:set var="laserPackageID" value="${laserService.getLaserPackageId(pkg.uuid)}"/>

            <g:if test="${laserPackageID}">
                <br><br>
                <a class="ui button" href="${laserService.getLaserPackageURL() + '/' + laserPackageID}" target="_blank">Show Package in Laser</a>
                <br>
                <br>
                <a class="ui button" href="${laserService.getLaserURL() + '/yoda/reloadPackage?packageUUID=' + pkg.uuid}"
                   target="_blank">Reload package data in Laser</a>

                <br>
                <br>
                <g:if test="${params.showLaserInfos == 'true'}">
                    <g:link class="ui button positive"
                            controller="${params.controller}"
                            action="${params.action}"
                            id="${displayobj.getOID()}"
                            params="${(request.param ?: [:])}">
                        Disable Laser Infos
                    </g:link>
                </g:if>
                <g:else>
                    <g:link class="ui button negative"
                            controller="${params.controller}"
                            action="${params.action}"
                            id="${displayobj.getOID()}"
                            params="${(request.param ?: [:]) + ["showLaserInfos": true]}">
                        Enable Laser Infos
                    </g:link>
                </g:else>
            </g:if>
        </p>
    </div>

    <br>
    <g:if test="${params.showLaserInfos == 'true'}">
        <div class="ui two cards">
            <div class="card">
                <div class="content">
                    <dl>
                        <dt class="control-label">Tipp Info in Laser:</dt>
                        <g:set var="tippUrl" value="${laserService.getLaserTippURL()}"/>
                        <g:set var="laserTipp" value="${laserService.getLaserTipp(tipp.uuid)}"/>
                        <g:if test="${laserTipp}">
                            <dd>
                                Name: <a href="${tippUrl + '/' + laserTipp.id}" target="_blank">${laserTipp.name}</a>
                            </dd>
                            <dd>
                                Status: ${laserTipp.status}
                            </dd>
                            <dd>
                                URL: ${laserTipp.url}
                            </dd>
                        </g:if>
                    </dl>
                </div>
            </div>

            <div class="card">
                <div class="content">
                    <dl>
                        <dt class="control-label">PT in Laser (Current):</dt>
                        <dd>
                            <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Current', tippId: tipp.id]" id="${pkg.id}">
                                ${laserService.permanentTitleInLaserCount(tipp.uuid, 'Current')}
                            </g:link>
                        </dd>
                        <dt class="control-label">PT in Laser (Retired):</dt>
                        <dd>
                            <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Retired', tippId: tipp.id]" id="${pkg.id}">
                                ${laserService.permanentTitleInLaserCount(tipp.uuid, 'Retired')}
                            </g:link>
                        </dd>
                        <dt class="control-label">PT in Laser (Expected):</dt>
                        <dd>
                            <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Expected', tippId: tipp.id]" id="${pkg.id}">
                                ${laserService.permanentTitleInLaserCount(tipp.uuid, 'Expected')}
                            </g:link>
                        </dd>
                        <dt class="control-label">PT in Laser (Deleted):</dt>
                        <dd>
                            <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Deleted', tippId: tipp.id]" id="${pkg.id}">
                                ${laserService.permanentTitleInLaserCount(tipp.uuid, 'Deleted')}
                            </g:link>
                        </dd>
                        <dt class="control-label">PT in Laser (Removed):</dt>
                        <dd>
                            <g:link controller="admin" action="permanentTitlesInLaser" params="[status: 'Removed', tippId: tipp.id]" id="${pkg.id}">
                                ${laserService.permanentTitleInLaserCount(tipp.uuid, 'Removed')}
                            </g:link>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </g:if>
</div>