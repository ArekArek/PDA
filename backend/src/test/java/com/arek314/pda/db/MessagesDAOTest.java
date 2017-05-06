package com.arek314.pda.db;

import com.arek314.pda.db.dao.MessagesDAO;
import com.arek314.pda.db.mapper.MessageMapper;
import com.arek314.pda.db.model.MessageModel;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagesDAOTest extends DAOTest {
    private MessagesDAO messagesDAO;

    @Override
    @BeforeTest
    public void buildDatabase() {
        super.buildDatabase();
        messagesDAO = dbi.onDemand(MessagesDAO.class);
    }

    @Override
    @BeforeMethod
    public void loadContent() throws Exception {
        super.loadContent();
    }

    private List<MessageModel> getMessagesFromDatabase() throws Exception {
        return getAllEntities(MessageModel.class, MessageMapper.class, "messages");
    }

    @Override
    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void getAllMessages() throws Exception {
        List<MessageModel> messagesFromDatabase = getMessagesFromDatabase();
        List<MessageModel> messages = messagesFromDatabase.stream().collect(Collectors.toList());

        assertThat(messagesDAO.getMessages()).containsAll(messages);
    }

    @Test
    public void createMessage() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = dateFormat.parse("03/03/2017 17:50:54");
        long time = date.getTime();
        final MessageModel messageModel = new MessageModel(111110, new Timestamp(time), "testSender", "hello");

        messagesDAO.createMessage(messageModel);
        List<MessageModel> messagesFromDatabase = getMessagesFromDatabase();
        messagesFromDatabase = messagesFromDatabase.stream().map(mm -> new MessageModel(mm.getUserId(), mm.getDate(), mm.getSender(), mm.getMessage())).collect(Collectors.toList());
        MessageModel lastMessage = messagesFromDatabase.get(messagesFromDatabase.size() - 1);

        assertThat(messagesFromDatabase).contains(messageModel);
        assertThat(lastMessage).isEqualToComparingFieldByField(messageModel);
    }

    @Test(priority = 1)
    public void deleteAll() throws Exception {
        messagesDAO.deleteAllMessages();

        assertThat(getMessagesFromDatabase()).isEmpty();
    }
}
