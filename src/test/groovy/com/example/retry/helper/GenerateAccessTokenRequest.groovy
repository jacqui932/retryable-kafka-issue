package com.example.retry.helper

import com.example.retry.IdentityRequest
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GenerateAccessTokenRequest extends MockRequest<GenerateAccessTokenRequest> {

    String traceId
    IdentityRequest identityRequest

    @Override
    protected boolean matches(GenerateAccessTokenRequest request) {
        return this == request
    }
}
