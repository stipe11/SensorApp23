package com.stipe.sensorapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(ignoreUnknown = true)
public class TheThingsNetworkResponse {

    @JsonProperty("payload_fields")
    public PayloadFields PayloadFields;
}
