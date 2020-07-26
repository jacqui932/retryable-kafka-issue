package com.example.retry;

import io.micronaut.context.annotation.Value;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.reactivex.Completable.complete;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Singleton
public class IdentityService {

    // Maximum time to give the call to Identity.
    //
    // App will take this from the expiry time of the token and call Identity approximately
    // this number of millis before the token expires.
    @Value("${application.identity.max-time-for-call}")
    private long maxTimeForCall;

    @Inject
    private IdentityClient identityClient;

    @Inject
    private IdentityServiceLogger logger;

    @Value("${application.identity.time-between-retries}")
    private long timeBetweenRetries;

    @Inject
    IdentityRequest identityRequest;

    IdentityResponse identityResponse;

    Disposable tokenRefresher;

    static boolean scheduleRetry = true;

    @PostConstruct
    void init() {
        tokenRefresher = generateAccessToken().subscribe();
    }

    @PreDestroy
    void cleanup() {
        // in test, when cleanup is running, there is a potential problem
        // that retry loop in generateAccessToken would start running forever
        // hence I unsubscribe when context get's destroyed
        if (tokenRefresher != null) {
            tokenRefresher.dispose();
        }
    }

    Completable generateAccessToken() {
        long timestamp = (identityResponse != null) ? identityResponse.expirationDateTime() :  System.currentTimeMillis();
        String traceId = "identity-service-" + timestamp;
        logger.onGenerateAccessToken(traceId);
        return identityClient.generateAccessToken(traceId, identityRequest)
                .doOnError(err -> logger.onCallingIdentityFail(err, traceId))
                .flatMapCompletable(response -> this.scheduleGenerateAccessToken(response, traceId))
                // retry forever
                .retryWhen(errors ->
                    // Count and increment the number of errors.
                    errors.map(error -> 1).scan(Integer::sum)
                        .doOnNext(errorCount -> logger.onRetry(errorCount, traceId))
                        .flatMapSingle(errorCount -> Single.timer(timeBetweenRetries, MILLISECONDS)));
    }

    private Completable scheduleGenerateAccessToken(IdentityResponse identityResponse, String traceId) {
        long nextUpdateTime = identityResponse.expirationDateTime() - maxTimeForCall;
        logger.onCallIdentitySuccess(nextUpdateTime, traceId);
        this.identityResponse = identityResponse;
        return scheduleGenerateAccessToken(nextUpdateTime);
    }

    private Completable scheduleGenerateAccessToken(long nextUpdateTime) {
        if (scheduleRetry) {
            long delay = nextUpdateTime - System.currentTimeMillis();
            if (delay > 0) {
                // we can wait a delay to still update the token before it's expiry
                return runDelayed(delay, MILLISECONDS, this::generateAccessToken);
            } else {
                // token already too old
                return generateAccessToken();
            }
        } else {
            return complete();
        }
    }

    /**
     * Defers and delays an action returning completable.
     */
    Completable runDelayed(long delay, TimeUnit unit, Callable<Completable> action) {
        return complete()
                .delay(delay, unit)
                .andThen(Completable.defer(action));
    }
}
