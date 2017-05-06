package com.arek314.pda;

import com.arek314.pda.service.PeopleExpirationConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PdaConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private PeopleExpirationConfiguration peopleExpirationConfiguration = new PeopleExpirationConfiguration();


    public DataSourceFactory getDatabase() {
        return database;
    }

    public PeopleExpirationConfiguration getPeopleExpirationConfiguration() {
        return peopleExpirationConfiguration;
    }
}
