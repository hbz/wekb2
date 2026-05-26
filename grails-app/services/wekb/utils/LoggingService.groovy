package wekb.utils

import grails.gorm.transactions.Transactional
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Transactional
class LoggingService {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()
    private final Map<String, RuntimeMeta> runtimeMeta = new ConcurrentHashMap<>()

    void setLevel(String loggerName, String levelName, String changedBy = 'system') {
        assert loggerName
        assert levelName


        cancelReset(loggerName)


        Level level = Level.valueOf(levelName.toUpperCase())
        Logger logger = getLogger(loggerName)
        logger.level = level


        runtimeMeta.put(loggerName, new RuntimeMeta(
                source: 'RUNTIME',
                previousLevel: null,
                expiresAt: null,
                changedBy: changedBy
        ))
    }

    void setLevelTemporarily(String loggerName, String levelName, long ttlSeconds, String changedBy = 'system') {
        assert loggerName
        assert levelName
        assert ttlSeconds > 0


        Logger logger = getLogger(loggerName)
        Level previousLevel = logger.level
        Level newLevel = Level.valueOf(levelName.toUpperCase())


        cancelReset(loggerName)
        logger.level = newLevel


        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds)


        ScheduledFuture task = scheduler.schedule({
            logger.level = previousLevel
            runtimeMeta.remove(loggerName)
        }, ttlSeconds, TimeUnit.SECONDS)


        runtimeMeta.put(loggerName, new RuntimeMeta(
                source: 'RUNTIME',
                previousLevel: previousLevel?.toString(),
                expiresAt: expiresAt,
                changedBy: changedBy,
                resetTask: task
        ))
    }

    void resetLevel(String loggerName) {
        assert loggerName
        cancelReset(loggerName)


        Logger logger = getLogger(loggerName)
        logger.level = null
        runtimeMeta.remove(loggerName)
    }

    Map getLevel(String loggerName) {
        Logger logger = getLogger(loggerName ?: Logger.ROOT_LOGGER_NAME)
        RuntimeMeta meta = runtimeMeta.get(logger.name)


        return [
                logger    : logger.name,
                configured: logger.level?.toString(),
                effective : logger.effectiveLevel.toString(),
                source    : meta ? 'RUNTIME' : 'FILE',
                expiresAt : meta?.expiresAt,
                changedBy : meta?.changedBy,
                previousLevel : meta?.previousLevel
        ]
    }

    List<Map> listLoggers() {
        def context = (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory()


        context.loggerList.collect { Logger l ->
            RuntimeMeta meta = runtimeMeta.get(l.name)
            [
                    logger    : l.name,
                    effective : l.effectiveLevel.toString(),
                    configured: l.level?.toString(),
                    source    : meta ? 'RUNTIME' : 'FILE',
                    expiresAt : meta?.expiresAt,
                    changedBy : meta?.changedBy,
                    previousLevel : meta?.previousLevel
            ]
        }.sort { it.logger }
    }


    private void cancelReset(String loggerName) {
        RuntimeMeta meta = runtimeMeta.remove(loggerName)
        if (meta?.resetTask) {
            meta.resetTask.cancel(false)
        }
    }


    private Logger getLogger(String name) {
        (Logger) LoggerFactory.getLogger(name)
    }


    private static class RuntimeMeta {
        String source
        String previousLevel
        Instant expiresAt
        String changedBy
        ScheduledFuture resetTask
    }

}
