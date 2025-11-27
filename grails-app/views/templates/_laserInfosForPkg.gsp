<g:set var="laserService" bean="${wekb.LaserService}"/>

<div class="ui warning icon message">

    <div class="content wekb-inline-lists">
        <div class="header">
            Package used in LAS:er: <g:link action="linkedSubsInLaser" controller="admin"
                                           id="${pkg.id}">${pkg.packageLinkedInLaserCount()}</g:link>
            <g:if test="${pkg.kbartSource}">
                <br>
                <br>
                <b><g:message code="kbartsource.kbartHasWekbFields"/>:</b> <semui:xEditableBoolean owner="${pkg.kbartSource}" field="kbartHasWekbFields" overwriteEditable="false"/> <i class="info ${pkg.kbartSource.kbartHasWekbFields ? 'red' : 'green'} big circle icon"></i>
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
                <a class="ui button" href="${laserService.getLaserURL() + '/yoda/reloadPackage?packageUUID=' + laserPackageID}"
                   target="_blank">Reload package data in Laser</a>
            </g:if>
        </p>
    </div>

    <br>

    <div class="ui two cards">
        <div class="card">
            <div class="content">
                <dl>
                    <dt class="control-label">Tipps in Laser (Current):</dt>
                    <dd>
                        ${laserService.tippsWithStatusInLaserCount(pkg.uuid, 'Current')}
                    </dd>
                    <dt class="control-label">Tipps in Laser (Retired):</dt>
                    <dd>
                        ${laserService.tippsWithStatusInLaserCount(pkg.uuid, 'Retired')}
                    </dd>
                    <dt class="control-label">Tipps in Laser (Expected):</dt>
                    <dd>
                        ${laserService.tippsWithStatusInLaserCount(pkg.uuid, 'Expected')}
                    </dd>
                    <dt class="control-label">Tipps in Laser (Deleted):</dt>
                    <dd>
                        ${laserService.tippsWithStatusInLaserCount(pkg.uuid, 'Deleted')}
                    </dd>
                    <dt class="control-label">Tipps in Laser (Removed):</dt>
                    <dd>
                        ${laserService.tippsWithStatusInLaserCount(pkg.uuid, 'Removed')}
                    </dd>
                </dl>
            </div>
        </div>

        <div class="card">
            <div class="content">
                <dl>
                    <dt class="control-label">Tipp (PT) in Laser (Current):</dt>
                    <dd>
                        <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                params="[perpetualAccess: params.perpetualAccess, status: 'Current']">
                            ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Current')}
                        </g:link>
                    </dd>
                    <dt class="control-label">Tipp (PT) in Laser (Retired):</dt>
                    <dd>
                        <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                params="[perpetualAccess: params.perpetualAccess, status: 'Retired']">
                            ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Retired')}
                        </g:link>
                    </dd>
                    <dt class="control-label">Tipp (PT) in Laser (Expected):</dt>
                    <dd>
                        <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                params="[perpetualAccess: params.perpetualAccess, status: 'Expected']">
                            ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Expected')}
                        </g:link>
                    </dd>
                    <dt class="control-label">Tipp (PT) in Laser (Deleted):</dt>
                    <dd>
                        <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                params="[perpetualAccess: params.perpetualAccess, status: 'Deleted']">
                            ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Deleted')}
                        </g:link>
                    </dd>
                    <dt class="control-label">Tipp (PT) in Laser (Removed):</dt>
                    <dd>
                        <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}"
                                params="[perpetualAccess: params.perpetualAccess, status: 'Removed']">
                            ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Removed')}
                        </g:link>
                    </dd>
                </dl>
            </div>
        </div>
    </div>
</div>