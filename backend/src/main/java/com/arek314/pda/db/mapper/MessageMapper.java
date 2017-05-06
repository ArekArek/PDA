package com.arek314.pda.db.mapper;

import com.arek314.pda.db.model.MessageModel;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageMapper implements ResultSetMapper<MessageModel> {

    @Override
    public MessageModel map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new MessageModel(resultSet.getInt("id"), resultSet.getInt("userId"), resultSet.getTimestamp("date"), resultSet.getString("sender"), resultSet.getString("message"));
    }
}
