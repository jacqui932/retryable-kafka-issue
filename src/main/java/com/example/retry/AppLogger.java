package com.example.retry;

import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;

@Singleton
public class AppLogger  {

    private static final String MY_PROJECT_LOG = "myProjectLog";
    private static final Logger log = LoggerFactory.getLogger(MY_PROJECT_LOG);

    public void info(Map<String, String> context, String message) {
        info(message, entries(context));
    }

    public void warn(Map<String, String> context, String message) {
        warn(message, entries(context));
    }

    public void error(Map<String, String> context, String message, Throwable t) {
        HashMap<String, String> args = new HashMap<>();
        args.put("error_type", t.getClass().getName());
        args.put("error_message", t.getMessage());
        args.putAll(context);
        error(message, entries(args), t);
    }

    protected void error(String message, StructuredArgument args, Throwable t) {
        log.error(message, args, t);
    }

    protected void warn(String message, StructuredArgument args) {
        log.warn(message, args);
    }

    protected void info(String str, StructuredArgument args) {
        log.info(str, args);
    }
}
