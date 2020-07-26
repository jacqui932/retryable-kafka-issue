package com.example.retry.helper

import com.example.retry.IdentityRequest
import com.example.retry.IdentityResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post

import static io.micronaut.http.HttpResponse.*
import static org.joda.time.DateTime.now

@Controller("/api/auth/oauth/v2/token")
class IdentityController {

    MockRequests<GenerateAccessTokenRequest, HttpResponse<String>> generateAccessToken = new MockRequests([
            successfulResponse: { ok(createSuccessfulResponse(now().plusSeconds(2).millis)) },
            serverError       : { serverError() },
            clientError       : { badRequest() }
    ])

    @Post
    HttpResponse<String> generateAccessToken(@Header("TraceId") String traceId,
                                             @Body IdentityRequest identityRequest) {
        generateAccessToken.process(new GenerateAccessTokenRequest([
                traceId        : traceId,
                identityRequest: identityRequest
        ]))
    }

    private static String createSuccessfulResponse(long timeInMillis) {
        def response = /
        {
            "access_token": "identityToken",
            "Claims": [
                {
                    "claimType": "${IdentityResponse.EXPIRATION_CLAIM}",
                    "value": "${timeInMillis / 1000 as Integer}"
                }
            ]
        }
        /
        response
    }

    def addGenerateAccessTokenResponse(ResponseStatus response) {
        generateAccessToken.addResponse(response)
    }
}
