package com.arek314.pda;

import com.arek314.pda.db.dao.InformationsDAO;
import com.arek314.pda.db.dao.MessagesDAO;
import com.arek314.pda.db.dao.PersonDAO;
import com.arek314.pda.resources.InformationsResource;
import com.arek314.pda.resources.MessagesResource;
import com.arek314.pda.resources.PeopleResource;
import com.arek314.pda.service.PeopleExpirationService;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class PdaApplication extends Application<PdaConfiguration> {

    private static final String HEALTH_CHECK_DATABASE_NAME = "database";

    public static void main(final String[] args) throws Exception {
        new PdaApplication().run(args);
    }

    @Override
    public String getName() {
        return "pda";
    }

    @Override
    public void initialize(final Bootstrap<PdaConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

        bootstrap.addBundle(new MigrationsBundle<PdaConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(PdaConfiguration configuration) {
                return configuration.getDatabase();
            }
        });

    }

    @Override
    public void run(final PdaConfiguration configuration, final Environment environment) {
        final DBI jdbi = new DBIFactory().build(environment, configuration.getDatabase(), HEALTH_CHECK_DATABASE_NAME);

        final PersonDAO personDAO = jdbi.onDemand(PersonDAO.class);
        final InformationsDAO informationsDAO = jdbi.onDemand(InformationsDAO.class);
        final MessagesDAO messagesDAO = jdbi.onDemand(MessagesDAO.class);

        PeopleResource peopleResource = new PeopleResource(personDAO);
        InformationsResource informationsResource = new InformationsResource(informationsDAO);
        MessagesResource messagesResource = new MessagesResource(messagesDAO);
        environment.jersey().register(peopleResource);
        environment.jersey().register(informationsResource);
        environment.jersey().register(messagesResource);

        environment.lifecycle().manage(new PeopleExpirationService(personDAO, configuration.getPeopleExpirationConfiguration()));
    }

}
