package com.arek314.pda.db.mapper;

import com.arek314.pda.db.model.Person;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements ResultSetMapper<Person> {

    @Override
    public Person map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
        return new Person(r.getInt("id"), r.getDouble("latitude"), r.getDouble("longitude"), r.getBoolean("isOnline"), r.getString("label"));
    }
}
