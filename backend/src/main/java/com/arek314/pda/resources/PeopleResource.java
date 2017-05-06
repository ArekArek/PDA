package com.arek314.pda.resources;

import com.arek314.pda.api.PersonRepresentation;
import com.arek314.pda.db.dao.PersonDAO;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleResource {
    private final PersonDAO personDAO;

    public PeopleResource(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GET
    public List<Object> allPeople(@QueryParam("id") int userId, @QueryParam("isOnline") boolean isOnline) {
        if (userId != 0) {
            if (isOnline == true) {
                return personDAO.getOnlinePeopleWithouthUser(userId).stream().map(person -> new PersonRepresentation(person)).collect(Collectors.toList());
            } else {
                return personDAO.getAllPeopleWithouthUser(userId).stream().map(person -> new PersonRepresentation(person)).collect(Collectors.toList());
            }
        } else {
            return personDAO.getAllPeople().stream().map(person -> new PersonRepresentation(person)).collect(Collectors.toList());
        }
    }

    @POST
    public Response createPerson(@Valid PersonRepresentation personRepresentation) {
        personDAO.createPerson(personRepresentation.map());
        return Response.ok(new PersonRepresentation(personRepresentation.map())).status(Response.Status.CREATED).build();
    }

    @PUT
    public Response updatePerson(@Valid PersonRepresentation personRepresentation) {
        personDAO.updatePerson(personRepresentation.map());
        return Response.ok(new PersonRepresentation(personRepresentation.map())).status(Response.Status.OK).build();
    }

    @DELETE
    public Response deletePerson(@QueryParam("id") int userId, @QueryParam("koniesa") String jakie) {
        if (userId != 0) {
            personDAO.deletePerson(userId);
            return Response.ok(Response.Status.OK).build();
        } else if (jakie.equals("tempe")) {
            personDAO.deleteAllPeople();
            return Response.ok(Response.Status.OK).build();
        } else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("/jsonExample")
    @GET
    public PersonRepresentation exampleJson() {
        PersonRepresentation personRepresentation = new PersonRepresentation(123654, 35.4455, 65.4869, false, "normal");

        return personRepresentation;
    }

}
