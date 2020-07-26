package com.example.retry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
public class IdentityServiceLogger {

    @Inject
    AppLogger appLogger;

    void onCallingIdentityFail(Throwable err, String traceId) {
        HashMap<String, String> args = new HashMap<>();
        args.put("trace_id", traceId);
        args.put("type", "identity");
        appLogger.error(args, "Error calling identity", err);
    }

    void onGenerateAccessToken(String traceId) {
        HashMap<String, String> args = new HashMap<>();
        args.put("trace_id", traceId);
        args.put("type", "identity");
        appLogger.info(args, "Attempting to retrieve a new token from identity");
    }

    void onRetry(Integer retryCount, String traceId) {
        HashMap<String, String> args = new HashMap<>();
        args.put("trace_id", traceId);
        args.put("type", "identity");
        args.put("retryCount", retryCount.toString());
        appLogger.warn(args, "Retrying call to identity");
    }

    void onCallIdentitySuccess(Long nextUpdateTime, String traceId) {
        HashMap<String, String> args = new HashMap<>();
        args.put("trace_id", traceId);
        args.put("type", "identity");
        args.put("token_update", nextUpdateTime.toString());
        appLogger.info(args, "Successfully fetched token from identity");
    }
}
