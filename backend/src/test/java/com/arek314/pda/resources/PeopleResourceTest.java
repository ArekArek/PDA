package com.arek314.pda.resources;

import com.arek314.pda.api.PersonRepresentation;
import com.arek314.pda.db.dao.PersonDAO;
import com.arek314.pda.db.model.Person;
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
public class PeopleResourceTest {

    @Captor
    private ArgumentCaptor<Person> personArgumentCaptor;

    private PersonRepresentation person;
    private PersonRepresentation person2;
    private PersonRepresentation person3;
    private List<PersonRepresentation> peopleRepresentation;
    private List<PersonRepresentation> peopleWithouthUsetRepresentation;
    private List<PersonRepresentation> onlinePeopleWithouthUsetRepresentation;
    private List<Person> people;
    private List<Person> peopleWithouthUser;
    private List<Person> onlinePeopleWithouthUser;
    private int id;
    private String peopleURI;
    private String peopleWithouthUserURI;
    private String onlinePeopleWithouthUserURI;
    private String deletePersonURI;
    private String deleteAllPeopleURI;

    private static final PersonDAO personDAO = mock(PersonDAO.class);

    @ClassRule
    public static final ResourceTestRule resourceTestRule = ResourceTestRule.builder().addProvider(new PeopleResource(personDAO)).build();

    @Before
    public void setUp() {
        id = 987789;
        setPeopleVariables();

        peopleURI = UriBuilder.fromResource(PeopleResource.class).build().toString();
        peopleWithouthUserURI = UriBuilder.fromResource(PeopleResource.class).queryParam("id", person.getId()).build().toString();
        onlinePeopleWithouthUserURI = UriBuilder.fromResource(PeopleResource.class).queryParam("id", person.getId()).queryParam("isOnline", true).build().toString();
        deletePersonURI = UriBuilder.fromResource(PeopleResource.class).queryParam("id", person.getId()).build().toString();
        deleteAllPeopleURI = UriBuilder.fromResource(PeopleResource.class).queryParam("koniesa", "tempe").build().toString();


        when(personDAO.getAllPeople()).thenReturn(people);
        when(personDAO.getAllPeopleWithouthUser(person.getId())).thenReturn(peopleWithouthUser);
        when(personDAO.getOnlinePeopleWithouthUser(person.getId())).thenReturn(onlinePeopleWithouthUser);
    }

    private void setPeopleVariables() {
        person = new PersonRepresentation(id, 32.4568, 98.7854, true, "normal");
        person2 = new PersonRepresentation(357452, 65.1411, 66.1002, false, "guest");
        person3 = new PersonRepresentation(332110, 11.2210, 32.0010, true, "boss");

        peopleRepresentation = new ArrayList<>();
        peopleRepresentation.add(person);
        peopleRepresentation.add(person2);
        peopleRepresentation.add(person3);

        people = new ArrayList<>();
        people.add(person.map());
        people.add(person2.map());
        people.add(person3.map());

        peopleWithouthUsetRepresentation = new ArrayList<>();
        peopleWithouthUsetRepresentation.add(person2);
        peopleWithouthUsetRepresentation.add(person3);

        peopleWithouthUser = new ArrayList<>();
        peopleWithouthUser.add(person2.map());
        peopleWithouthUser.add(person3.map());

        onlinePeopleWithouthUsetRepresentation = new ArrayList<>();
        onlinePeopleWithouthUsetRepresentation.add(person3);

        onlinePeopleWithouthUser = new ArrayList<>();
        onlinePeopleWithouthUser.add(person3.map());
    }

    static private List<PersonRepresentation> getListFromResponse(String uri) {
        final Response response = resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        return response.readEntity(new GenericType<List<PersonRepresentation>>() {
        });
    }

    static private Response postPerson(String uri, int id, double latitude, double longitude, boolean isOnline, String label) {
        final Response response = resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(new PersonRepresentation(id, latitude, longitude, isOnline, label), MediaType.APPLICATION_JSON_TYPE));
        return response;
    }

    static private Response putPerson(String uri, PersonRepresentation person) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE));
    }

    static private Response deletePerson(String uri) {
        return resourceTestRule.client().target(uri).request(MediaType.APPLICATION_JSON_TYPE).delete();
    }

    @After
    public void tearDown() {
        reset(personDAO);
    }

    @Test
    public void listPeople() {
        final List<PersonRepresentation> response = getListFromResponse(peopleURI);

        verify(personDAO).getAllPeople();
        assertThat(response).containsAll(peopleRepresentation);
    }

    @Test
    public void listPeopleWithouthUser() {
        final List<PersonRepresentation> response = getListFromResponse(peopleWithouthUserURI);

        verify(personDAO).getAllPeopleWithouthUser(person.getId());
        assertThat(response).doesNotContain(person);
        assertThat(response).containsAll(peopleWithouthUsetRepresentation);
    }

    @Test
    public void listOnlinePeopleWithouthUser() {
        final List<PersonRepresentation> response = getListFromResponse(onlinePeopleWithouthUserURI);
        verify(personDAO).getOnlinePeopleWithouthUser(person.getId());
        assertThat(response).doesNotContain(person);
        assertThat(response).containsAll(onlinePeopleWithouthUsetRepresentation);
    }

    @Test
    public void createPerson() {
        final Response response = postPerson(peopleURI, person.getId(), person.getLatitude(), person.getLongitude(), person.isOnline(), person.getLabel());

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.CREATED);
        verify(personDAO).createPerson(personArgumentCaptor.capture());
        assertThat(personArgumentCaptor.getValue().getId()).isNotNull();
        assertThat(personArgumentCaptor.getValue().getLatitude()).isEqualTo(person.getLatitude());
        assertThat(personArgumentCaptor.getValue().getLongitude()).isEqualTo(person.getLongitude());
    }

    @Test
    public void updatePerson() {
        final Response response = putPerson(peopleURI, new PersonRepresentation(person.getId(), person.getLatitude(), person.getLongitude(), person.isOnline(), person.getLabel()));
        final PersonRepresentation updatedPerson = response.readEntity(PersonRepresentation.class);
        verify(personDAO).updatePerson(updatedPerson.map());
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void removePerson() {
        final Response response = deletePerson(deletePersonURI);

        verify(personDAO).deletePerson(id);
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void removeAll() {
        final Response response = deletePerson(deleteAllPeopleURI);

        verify(personDAO).deleteAllPeople();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
    }


}