<g:set var="laserService" bean="${wekb.LaserService}"/>

<div class="ui warning icon message">

    <div class="content wekb-inline-lists">
        <div class="header">
            Package used in LAS:er: <g:link action="linkedSubsInLaser" controller="admin"
                                           id="${pkg.id}">${pkg.packageLinkedInLaserCount()}</g:link>
        </div>

        <p>
        (PerpetualAccess: <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}" params="[perpetualAccess: true]">${laserService.packageLinkedWithPerpetualAccessInLaserCount(pkg.uuid)}</g:link> /
        Not PerpetualAccess: <g:link action="linkedSubsInLaser" controller="admin" id="${pkg.id}" params="[perpetualAccess: false]">${laserService.packageLinkedWithOutPerpetualAccessInLaserCount(pkg.uuid)}</g:link>)
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
                    <dt class="control-label">PT in Laser (Current):</dt>
                    <dd>
                        ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Current')}
                    </dd>
                    <dt class="control-label">PT in Laser (Retired):</dt>
                    <dd>
                        ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Retired')}
                    </dd>
                    <dt class="control-label">PT in Laser (Expected):</dt>
                    <dd>
                        ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Expected')}
                    </dd>
                    <dt class="control-label">PT in Laser (Deleted):</dt>
                    <dd>
                        ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Deleted')}
                    </dd>
                    <dt class="control-label">PT in Laser (Removed):</dt>
                    <dd>
                        ${laserService.permanentTitlesWithStatusInLaserCount(pkg.uuid, 'Removed')}
                    </dd>
                </dl>
            </div>
        </div>
    </div>
</div>