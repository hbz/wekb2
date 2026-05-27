package wekb.utils

import grails.util.Holders
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import wekb.Identifier
import wekb.TitleInstancePackagePlatform
import wekb.annotations.TrigramIndex
import wekb.annotations.TrigramLowerIndex
import wekb.helper.BeanStore

import java.lang.annotation.Annotation
import java.lang.reflect.Field

@Slf4j
class DatabaseUtils {

    public static final List<Class> TRIGRAM_ANNOTATIONS = [TrigramIndex, TrigramLowerIndex]
    public static final List<Class> TRIGRAM_DOMAIN_CLASSES = [Identifier, wekb.Package, TitleInstancePackagePlatform]

    static void initTrigramIndices() {

        groovy.sql.Sql sql = new groovy.sql.Sql(BeanStore.getDataSource())

        // postgresql<version>-contrib required
        sql.withTransaction {
            sql.executeUpdate('CREATE EXTENSION if not exists pg_trgm;')
            sql.commit()
        }

        List<List> trgmList = []

        TRIGRAM_ANNOTATIONS.each { Class tiClazz ->
            CodeUtils.getDomainClassesByAttributeAnnotation(tiClazz).each { Class dcClazz ->
                dcClazz.declaredFields.each { Field field ->
                    Annotation tia = field.getAnnotation(tiClazz)
                    if(tia) {
                        PersistentEntity pe = Holders.grailsApplication.mappingContext.getPersistentEntity(dcClazz.name)
                        PersistentProperty pep = pe.persistentProperties.find { PersistentProperty pp -> pp.name == field.name }
                        String tableName = pe.getMapping().getMappedForm().getProperty('table').name ?: dcClazz.simpleName.replaceAll(/([a-z])([A-Z])/,'$1_$2').toLowerCase()
                        String fieldName = pep.getMapping().getMappedForm().getProperty('columns').first().name
                        if(tiClazz == TrigramIndex) {
                            String ginOps = tia.collation() != '' ? fieldName + ' collate "' + tia.collation() + '"' : fieldName
                            trgmList << [tableName, fieldName, fieldName + '_idx_trigram', ginOps]
                        }
                        else if(tiClazz == TrigramLowerIndex) {
                            String ginOps = 'lower(' + fieldName + '::text)' + (tia.collation() != '' ? ' collate "' + tia.collation() + '"' : '')
                            trgmList << [tableName, fieldName, fieldName + '_idx_lower_trigram', ginOps]
                        }
                    }
                }
            }
        }

        trgmList.each { List trgm ->
            String tbl = trgm[0]
            String fie = trgm[1]
            String idx = trgm[2]
            String gin = trgm[3]

            boolean idxMatch = sql.rows("select * from pg_stat_all_indexes idx join pg_class c on idx.relid = c.oid where idx.relname = '" + tbl + "' and indexrelname = '" + idx + "'")

            if(!idxMatch) {
                sql.withTransaction {
                    String cmd = 'create index ' + idx + ' on ' + tbl + ' using gin (' + gin + ' gin_trgm_ops);'
                    log.info(' -> ' + tbl + '.' + fie + ' : ' + idx)
                    sql.executeUpdate(cmd)
                    sql.commit()
                }
            }
        }

    }

}
