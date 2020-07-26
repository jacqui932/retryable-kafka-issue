package com.example.retry

import com.example.retry.helper.IdentityController
import com.example.retry.helper.MockAppLogger
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import javax.inject.Inject

import static com.example.retry.helper.ResponseStatus.*

@MicronautTest
class RetrySpec extends Specification {

    @Inject
    MockAppLogger appLogger

    @Inject
    IdentityController identityController

    @Inject
    IdentityService identityService

    PollingConditions conditions = new PollingConditions(timeout: 10)

    def 'When the call to identity fails several times, the call to the aqueduct should wait'() {
        when:
        identityController.addGenerateAccessTokenResponse(SERVER_ERROR)
        identityController.addGenerateAccessTokenResponse(SERVER_ERROR)
        conditions.eventually {
            appLogger.containsError(HttpClientResponseException)
        }

        then:
        conditions.eventually {
            appLogger.findAll([level: "ERROR", error_message: "Internal Server Error"], "Error calling identity").size() == 2
            appLogger.containsLog("Successfully fetched token from identity")
        }
    }
}
