package com.example.retry.helper

import com.example.retry.AppLogger
import io.micronaut.context.annotation.Replaces
import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.marker.MapEntriesAppendingMarker

import javax.inject.Singleton
import java.util.concurrent.ConcurrentLinkedQueue

// Define a mock app logger so that we can check messages are written out as expected
@Replaces(AppLogger.class)
@Singleton
class MockAppLogger extends AppLogger {

    Collection<LogMessage> logMessages = new ConcurrentLinkedQueue<>()

    @Override
    protected void info(String msg, StructuredArgument args) {
        super.info(msg, args)
        addLog(msg, args, "INFO")
    }

    @Override
    protected void warn(String msg, StructuredArgument args) {
        super.warn(msg, args)
        addLog(msg, args, "WARN")
    }

    @Override
    protected void error(String msg, StructuredArgument args, Throwable t) {
        super.error(msg, args, t)
        addLog(msg, args, "ERROR")
    }

    private addLog(String message, StructuredArgument args, String level) {
        assert args instanceof MapEntriesAppendingMarker
        this.logMessages.add(new LogMessage([key: message, context: args.map + [level: level]]))
    }

    boolean containsError(Class<? extends Exception> exceptionType) {
        this.logMessages.any { it.isError(exceptionType) }
    }

    boolean containsError(Class<? extends Exception> exceptionType, String traceId) {
        containsErrorXTimes(exceptionType, traceId, 1)
    }

    boolean containsErrorXTimes(Class<? extends Exception> exceptionType, String traceId, int times) {
        def logMessage = this.logMessages.findAll { it.isError(exceptionType) && it.traceIdContains(traceId) }
        return logMessage.size() == times
    }

    boolean contains ( Map context, String message) {
        this.logMessages.any { it.contains(context, message) }
    }

    Collection<LogMessage> findAll(Map context, String message) {
        this.logMessages.findAll { it.contains(context, message) }
    }

    boolean containsLog(String message) {
        this.logMessages.findAll { it.isMessage(message) }.size() == 1
    }

    boolean containsLog(String message, String traceId) {
        def logMessage = this.logMessages.findAll { it.isMessage(message) && it.traceIdContains(traceId) }
        assert logMessage.size() == 1
        return true
    }

    boolean clear() {
        this.logMessages.clear()
    }

    @Override
    String toString() {
        "LogMessages - first 100 messages: [ \n" + logMessages.take(100).join("\n") + "\n]\n"
    }
}
