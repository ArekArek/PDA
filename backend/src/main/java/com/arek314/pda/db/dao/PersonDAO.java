package com.arek314.pda.db.dao;

import com.arek314.pda.db.mapper.PersonMapper;
import com.arek314.pda.db.model.Person;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

@RegisterMapper(PersonMapper.class)
public abstract class PersonDAO {

    @SqlQuery("select id, latitude, longitude, \"isOnline\", label from people")
    public abstract Collection<Person> getAllPeople();

    @SqlQuery("select id, latitude, longitude, \"isOnline\", label from people where id <> :userId")
    public abstract Collection<Person> getAllPeopleWithouthUser(@Bind("userId") int userId);

    @SqlQuery("select id, latitude, longitude, \"isOnline\", label from people where id <> :userId " + "AND \"isOnline\" = 'true'")
    public abstract Collection<Person> getOnlinePeopleWithouthUser(@Bind("userId") int userId);

    @SqlUpdate("insert into people (id, latitude, longitude, \"isOnline\", label, \"expirationDate\") " + "values (:id, :latitude, :longitude, :isOnline, :label, :expirationDate)")
    abstract int create(@BindBean Person person, @Bind("expirationDate") Date expirationDate);

    @SqlUpdate("update people set latitude = :latitude, longitude = :longitude, \"isOnline\" = " + ":isOnline, label =:label, \"expirationDate\" = :expirationDate where id = :id")
    abstract void update(@BindBean Person person, @Bind("expirationDate") Date expirationDate);

    @SqlUpdate("delete from people where id = :userId")
    public abstract void deletePerson(@Bind("userId") int userId);

    @SqlUpdate("delete from people")
    public abstract void deleteAllPeople();

    @SqlQuery("select id, latitude, longitude, \"isOnline\", label from people where id = :userId")
    public abstract Person getPerson(@Bind("userId") int userId);

    @SqlUpdate("update people set \"isOnline\" = 'false' where \"isOnline\" = 'true' and \"expirationDate\" " + "<= :date")
    abstract void disconnectExpiredPerson(@Bind("date") Date date);

    public void disconnectExpired(int validityTime) {
        Date date = Date.from(Instant.now().minus(validityTime, ChronoUnit.SECONDS));
        disconnectExpiredPerson(date);
    }

    public void createPerson(Person person) {
        create(person, new Date());
    }

    public void updatePerson(Person person) {

        if (getPerson(person.getId()) != null) {
            update(person, new Date());
        } else create(person, new Date());

    }


}
