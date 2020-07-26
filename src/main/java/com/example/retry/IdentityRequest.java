package com.example.retry;

import io.micronaut.context.annotation.Value;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;

@Singleton
@EqualsAndHashCode
@Getter
@Setter
public class IdentityRequest {
    private final String grant_type = "password";
    private final String scope = "service";

    @Value("${application.identity.clientid}")
    private String client_id;

    @Value("${application.identity.username}")
    private String username;

    @Value("${application.identity.password}")
    private String password;
}
