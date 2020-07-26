package com.example.retry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@ToString
@Getter
public class IdentityResponse {

    public static final String EXPIRATION_CLAIM = "http://schemas.microsoft.com/ws/2008/06/identity/claims/expiration";

    private final String access_token;

    @JsonProperty("Claims")
    private final Collection<Claim> Claims;

    @JsonCreator
    public IdentityResponse(
            @JsonProperty(value = "access_token", required = true) String access_token,
            @JsonProperty(value = "Claims", required = true) Collection<Claim> Claims
    ) {
        this.access_token = access_token;
        this.Claims = Claims;
    }

    public long expirationDateTime() {
        return Long.parseLong(Claims.stream().filter(it -> it.getClaimType().equals(EXPIRATION_CLAIM))
                .findFirst().get().getValue()) * 1000;
    }
}
