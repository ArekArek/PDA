package com.arek314.pda.service;

import com.arek314.pda.db.dao.PersonDAO;
import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeopleExpirationService implements Managed {
    final private PersonDAO personDAO;
    private final ScheduledExecutorService service;
    private final PeopleExpirationConfiguration peopleExpirationConfiguration;

    public PeopleExpirationService(PersonDAO personDAO, PeopleExpirationConfiguration peopleExpirationConfiguration) {
        this.personDAO = personDAO;
        this.peopleExpirationConfiguration = peopleExpirationConfiguration;
        service = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void start() throws Exception {
        System.out.println("PeopleExpirationService starts");
        service.scheduleAtFixedRate(new ExpirePeople(), peopleExpirationConfiguration.getPeriod(), peopleExpirationConfiguration.getPeriod(), TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("PeopleExpirationService starts");
        service.shutdown();
    }

    private class ExpirePeople implements Runnable {

        private ExpirePeople() {
        }

        @Override
        public void run() {
            personDAO.disconnectExpired(peopleExpirationConfiguration.getValidityTime());
        }
    }
}
