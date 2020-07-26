package com.example.retry.helper

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.ToString

@Canonical
@CompileStatic
@ToString(includePackage = false)
class LogMessage {

    String key
    Map context

    boolean isError(Class<? extends Throwable> throwable) {
        context.error_type == throwable.name
    }

    boolean isMessage(String message) {
        key == message
    }

    boolean traceIdContains(String traceId) {
        ((String) context.trace_id).contains(traceId)
    }

    boolean contains(Map map, String message) {
        key.equals(message) && context.subMap(map.keySet()) == map
    }
}
