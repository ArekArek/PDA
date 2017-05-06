package com.arek314.pda.resources;

import com.arek314.pda.api.InformationRepresentation;
import com.arek314.pda.db.dao.InformationsDAO;
import com.arek314.pda.db.model.Information;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InformationResourceTest {

    @Captor
    private ArgumentCaptor<Information> informationArgumentCaptor;
    private static final InformationsDAO informationsDAO = mock(InformationsDAO.class);
    private String informationURI;
    private String getAllInformationsURI;
    private String deleteInformationsURI;
    private InformationRepresentation informationRepresentation1;
    private InformationRepresentation informationRepresentation2;
    private InformationRepresentation informationRepresentation3;
    private Information information1;
    private Information information2;
    private Information information3;

    private List<InformationRepresentation> informationRepresentations;
    private List<Information> informations;


    @ClassRule
    public static final ResourceTestRule resourceTestRule = ResourceTestRule.builder().addProvider(new InformationsResource(informationsDAO)).build();

    @Before
    public void setUp() {
        informationURI = UriBuilder.fromResource(InformationsResource.class).build().toString();
        getAllInformationsURI = UriBuilder.fromResource(InformationsResource.class).path("/all").build().toString();
        deleteInformationsURI = UriBuilder.fromResource(InformationsResource.class).queryParam("koniesa", "tempe").build().toString();

        setVariables();


        when(informationsDAO.getAllInformations()).thenReturn(informations);
    }

    private void setVariables() {
        informationRepresentation1 = new InformationRepresentation(1, "http://www.example.com/sample.png");
        informationRepresentation2 = new InformationRepresentation(2, "https://sample.com/example.png");
        informationRepresentation3 = new InformationRepresentation(3, "http://pample.net/sepample.png");
        information1 = informationRepresentation1.map();
        information2 = informationRepresentation2.map();
        information3 = informationRepresentation3.map();

        informationRepresentations = new ArrayList<>();
        informationRepresentations.add(informationRepresentation1);
        informationRepresentations.add(informationRepresentation2);
        informationRepresentations.add(informationRepresentation3);

        informations = new ArrayList<>();
        informations.add(information1);
        informations.add(information2);
        informations.add(information3);
    }

    @After
    public void tearDown() {
        reset(informationsDAO);
    }

    private static InformationRepresentation getRepresentationFromResponse(String uri) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(InformationRepresentation.class);
    }

    private static List<InformationRepresentation> getRepresentationsListFromResponse(String uri) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(new GenericType<List<InformationRepresentation>>() {
        });
    }

    private static Response postInformation(String uri, int id, String mapURL) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(new InformationRepresentation(id, mapURL), MediaType.APPLICATION_JSON_TYPE));
    }

    private static Response putInformation(String uri, int id, String mapURL) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(new InformationRepresentation(id, mapURL), MediaType.APPLICATION_JSON_TYPE));
    }

    private static Response deleteInformation(String uri) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).delete();
    }

    @Test
    public void getInformation() {
        when(informationsDAO.getInformation()).thenReturn(information3);
        final InformationRepresentation response = getRepresentationFromResponse(informationURI);

        verify(informationsDAO).getInformation();
        assertThat(response).isEqualToComparingFieldByField(informationRepresentation3);
    }

    @Test
    public void getAllInformations() {
        final List<InformationRepresentation> response = getRepresentationsListFromResponse(getAllInformationsURI);

        verify(informationsDAO).getAllInformations();
        assertThat(response).isEqualTo(informationRepresentations);
    }

    @Test
    public void createInformation() {
        final Response response = postInformation(informationURI, informationRepresentation2.getId(), informationRepresentation2.getMapURL());

        verify(informationsDAO).createInformation(informationArgumentCaptor.capture());
        assertThat(Response.Status.CREATED).isEqualTo(response.getStatusInfo());
        assertThat(informationArgumentCaptor.getValue()).isEqualToComparingFieldByField(information2);
    }

    @Test
    public void updateInformation() {
        final Response response = putInformation(informationURI, informationRepresentation2.getId(), informationRepresentation2.getMapURL());

        verify(informationsDAO).updateInformation(informationArgumentCaptor.capture());
        assertThat(Response.Status.OK).isEqualTo(response.getStatusInfo());
        assertThat(informationArgumentCaptor.getValue().getMapURL()).isEqualTo(informationRepresentation2.getMapURL());
        assertThat(informationArgumentCaptor.getValue().getId()).isEqualTo(informationRepresentation2.getId());

    }

    @Test
    public void deleteAllInformations() {
        final Response response = deleteInformation(deleteInformationsURI);

        verify(informationsDAO).deleteAll();
        assertThat(Response.Status.OK).isEqualTo(response.getStatusInfo());
    }

    @Test
    public void getInformationFromEmpty() {
        when(informationsDAO.getInformation()).thenReturn(null);
        final InformationRepresentation response = getRepresentationFromResponse(informationURI);

        verify(informationsDAO).getInformation();
        assertThat(response).isEqualToComparingFieldByField(new InformationRepresentation());
    }
}
