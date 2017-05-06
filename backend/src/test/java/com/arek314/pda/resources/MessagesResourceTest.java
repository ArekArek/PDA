package com.arek314.pda.resources;

import com.arek314.pda.api.MessageRepresentation;
import com.arek314.pda.db.dao.MessagesDAO;
import com.arek314.pda.db.model.MessageModel;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessagesResourceTest {
    @Captor
    private ArgumentCaptor<MessageModel> messageArgumentCaptor;
    private static final MessagesDAO messagesDAO = mock(MessagesDAO.class);
    private String messagesURI;
    private String deleteAllMessagesURI;
    private MessageRepresentation messageRepresentation1;
    private MessageRepresentation messageRepresentation2;
    private MessageRepresentation messageRepresentation3;
    private MessageModel messageModel1;
    private MessageModel messageModel2;
    private MessageModel messageModel3;

    private List<MessageRepresentation> messageRepresentations;
    private List<MessageModel> messageModels;


    @ClassRule
    public static final ResourceTestRule resourceTestRule = ResourceTestRule.builder().addProvider(new MessagesResource(messagesDAO)).build();

    @Before
    public void setUp() {
        messagesURI = UriBuilder.fromResource(MessagesResource.class).build().toString();
        deleteAllMessagesURI = UriBuilder.fromResource(MessagesResource.class).queryParam("koniesa", "tempe").build().toString();
        setVariables();


        when(messagesDAO.getMessages()).thenReturn(messageModels);
    }

    private void setVariables() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;

        try {
            date = dateFormat.parse("2017-02-03 17:23:11");
            messageRepresentation1 = new MessageRepresentation(1, 123321, new Timestamp(date.getTime()), "testSender1", "attention - testing");

            date = dateFormat.parse("2017-02-03 17:26:34");
            messageRepresentation2 = new MessageRepresentation(2, 987741, new Timestamp(date.getTime()), "testSender2", "do you hear that");

            date = dateFormat.parse("2017-02-03 17:26:52");
            messageRepresentation3 = new MessageRepresentation(3, 965231, new Timestamp(date.getTime()), "testSender3", "hurray, its working");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        messageModel1 = messageRepresentation1.map();
        messageModel2 = messageRepresentation2.map();
        messageModel3 = messageRepresentation3.map();

        messageRepresentations = new ArrayList<>();
        messageRepresentations.add(messageRepresentation1);
        messageRepresentations.add(messageRepresentation2);
        messageRepresentations.add(messageRepresentation3);

        messageModels = new ArrayList<>();
        messageModels.add(messageModel1);
        messageModels.add(messageModel2);
        messageModels.add(messageModel3);
    }

    @After
    public void tearDown() {
        reset(messagesDAO);
    }

    private static List<MessageRepresentation> getRepresentationsListFromResponse(String uri) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(new GenericType<List<MessageRepresentation>>() {
        });
    }

    private static Response postMessage(String uri, int id, int userId, Timestamp date, String sender, String message) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(new MessageRepresentation(id, userId, date, sender, message), MediaType.APPLICATION_JSON_TYPE));
    }

    private static Response deleteMessages(String uri) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).delete();
    }

    @Test
    public void getAllMessages() {
        final List<MessageRepresentation> response = getRepresentationsListFromResponse(messagesURI);

        verify(messagesDAO).getMessages();
        assertThat(response).isEqualTo(messageRepresentations);
    }

    @Test
    public void createMessage() {
        final Response response = postMessage(messagesURI, messageRepresentation2.getId(), messageRepresentation2.getUserId(), messageRepresentation2.getDate(), messageRepresentation2.getSender(), messageRepresentation2.getMessage());

        verify(messagesDAO).createMessage(messageArgumentCaptor.capture());
        assertThat(Response.Status.CREATED).isEqualTo(response.getStatusInfo());
        assertThat(messageArgumentCaptor.getValue()).isEqualToComparingFieldByField(messageModel2);
    }

    @Test
    public void deleteAllMessages() {
        final Response response = deleteMessages(deleteAllMessagesURI);

        verify(messagesDAO).deleteAllMessages();
        assertThat(Response.Status.OK).isEqualTo(response.getStatusInfo());
    }
}
