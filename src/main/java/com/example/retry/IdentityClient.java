package com.example.retry;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

@Client("${application.identity.url}")
@Header(name = "Content-Type", value = "application/json")
public interface IdentityClient {

    @Post("/api/auth/oauth/v2/token")
    Single<IdentityResponse> generateAccessToken(
            @Header("TraceId") String traceId,
            @Body IdentityRequest identityRequest
    );
}
