package com.arek314.pda.db.dao;

import com.arek314.pda.db.mapper.MessageMapper;
import com.arek314.pda.db.model.MessageModel;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.Collection;

@RegisterMapper(MessageMapper.class)
public abstract class MessagesDAO {

    @SqlQuery("select * from messages")
    public abstract Collection<MessageModel> getMessages();

    @SqlUpdate("insert into messages (\"userId\", date, sender, message) values (:userId, :date, :sender, :message)")
    abstract void insertMessage(@BindBean MessageModel messageModel);

    @SqlUpdate("delete from messages")
    public abstract void deleteAllMessages();

    public void createMessage(MessageModel messageModel) {
        messageModel.setDate(new Timestamp(System.currentTimeMillis()));
        insertMessage(messageModel);
    }
}
