package com.example.retry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Claim {

    private final String claimType;
    private final String value;

    @JsonCreator
    public Claim(
            @JsonProperty(value = "claimType", required = true) String claimType,
            @JsonProperty(value = "value", required = true) String value
    ) {
        this.claimType = claimType;
        this.value = value;
    }
}
