package com.arek314.pda.service;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PeopleExpirationConfiguration extends Configuration {
    @Valid
    @NotNull
    private int period;

    @Valid
    @NotNull
    private int validityTime;

    public PeopleExpirationConfiguration() {
    }

    public int getPeriod() {
        return period;
    }

    public int getValidityTime() {
        return validityTime;
    }
}
