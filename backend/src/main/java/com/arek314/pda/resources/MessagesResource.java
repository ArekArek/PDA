package com.arek314.pda.resources;

import com.arek314.pda.api.MessageRepresentation;
import com.arek314.pda.db.dao.MessagesDAO;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessagesResource {
    private MessagesDAO messagesDAO;

    public MessagesResource(MessagesDAO messagesDAO) {
        this.messagesDAO = messagesDAO;
    }

    @GET
    public List<Object> getAllMessages() {
        return messagesDAO.getMessages().stream().map(messageModel -> new MessageRepresentation(messageModel)).collect(Collectors.toList());
    }

    @POST
    public Response createMessage(@Valid MessageRepresentation messageRepresentation) {
        messagesDAO.createMessage(messageRepresentation.map());
        return Response.ok(new MessageRepresentation(messageRepresentation.map())).status(Response.Status.CREATED).build();
    }

    @DELETE
    public Response deleteMessage(@QueryParam("koniesa") String jakie) {
        if (jakie.equals("tempe")) {
            messagesDAO.deleteAllMessages();
            return Response.ok(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
