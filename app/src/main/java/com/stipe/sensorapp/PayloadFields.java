package com.stipe.sensorapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayloadFields {

    public String getDegressC() {
        return degressC;
    }

    public void setDegressC(String degressC) {
        this.degressC = degressC;
    }

    @JsonProperty("degreesC")
    public String degressC;


    public String getHumidity() {
        return Humidity;
    }

    public void setHumidity(String humidity) {
        Humidity = humidity;
    }

    public String Humidity;

}
