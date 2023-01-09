http://grails-plugins.github.io/grails-database-migration/3.0.x/index.html#configuration


## **Setup**

###### **build.gradle:**

buildscript {
   dependencies {
      ...
      classpath 'org.grails.plugins:database-migration:3.0.4'
   }
}

dependencies {
   ...
     compile 'org.grails.plugins:database-migration:3.0.4'
}

dependencies {
   ...
     compile 'org.liquibase:liquibase-core:3.5.5'
}

sourceSets {
    main {
        resources {
            srcDir 'grails-app/migrations'
        }
    }
}

###### **application.groovy:**

grails.plugin.databasemigration.updateOnStart = true

###### **application.yml:**
all dbCreate to dbCreate: none


## **Create changelog after changes on Domain Classes**
1. `grails prod dbm-gorm-diff changelog-2021-03-29.groovy`
2. check changelog-2021-03-29.groovy if all changes are included
3. include changelog-2021-03-29.groovy in changelog.groovy

## **Update database with new changelogs**
1. use `run app` or `grails prod dbm-update`




