package com.arek314.pda.db.mapper;

import com.arek314.pda.db.model.Information;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InformationMapper implements ResultSetMapper<Information> {
    @Override
    public Information map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new Information(resultSet.getInt("id"), resultSet.getString("mapURL"));
    }
}
