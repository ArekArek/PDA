package com.arek314.pda.resources;

import com.arek314.pda.api.InformationRepresentation;
import com.arek314.pda.db.dao.InformationsDAO;
import com.arek314.pda.db.model.Information;

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

@Path("/information")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InformationsResource {
    private final InformationsDAO informationsDAO;

    public InformationsResource(InformationsDAO informationsDAO) {
        this.informationsDAO = informationsDAO;
    }

    @GET
    public InformationRepresentation getInformation() {
        Information info = informationsDAO.getInformation();
        return new InformationRepresentation(info);
    }

    @GET
    @Path("/all")
    public List<Object> getAllInformations() {
        return informationsDAO.getAllInformations().stream().map(information -> new InformationRepresentation(information)).collect(Collectors.toList());
    }

    @POST
    public Response createInformation(@Valid InformationRepresentation informationRepresentation) {
        informationsDAO.createInformation(informationRepresentation.map());
        return Response.ok(new InformationRepresentation(informationRepresentation.map())).status(Response.Status.CREATED).build();
    }

    @PUT
    public Response updateInformation(@Valid InformationRepresentation informationRepresentation) {
        informationsDAO.updateInformation(informationRepresentation.map());
        return Response.ok(new InformationRepresentation(informationRepresentation.map())).status(Response.Status.OK).build();
    }

    @DELETE
    public Response deleteInformations(@QueryParam("koniesa") String jakie) {
        if (jakie.equals("tempe")) {
            informationsDAO.deleteAll();
            return Response.ok(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
