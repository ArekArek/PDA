package com.arek314.pda.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class PersonRepresentationTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private final int FIXTURE_PERSON_ID = 123456;
    private final double FIXTURE_PERSON_LATITUDE = 12.3254;
    private final double FIXTURE_PEROSN_LONGITUDE = 35.47856;
    private final boolean FIXTURE_PERSON_ONLINE = true;
    private final String FIXTURE_PERSON_LABEL = "normal";
    private PersonRepresentation personRepresentation;

    @Before
    public void setup() {
        personRepresentation = new PersonRepresentation(FIXTURE_PERSON_ID, FIXTURE_PERSON_LATITUDE, FIXTURE_PEROSN_LONGITUDE, FIXTURE_PERSON_ONLINE, FIXTURE_PERSON_LABEL);
    }

    @Test
    public void serializeToJSON() throws Exception {
        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/personRepresentation.json"), PersonRepresentation.class));

        assertThat(MAPPER.writeValueAsString(personRepresentation)).isEqualTo(expected);
    }

    @Test
    public void deserializeFromJSON() throws Exception {
        PersonRepresentation newPersonRepresentation = MAPPER.readValue(fixture("fixtures/personRepresentation.json"), PersonRepresentation.class);
        assertThat(newPersonRepresentation).isEqualToComparingFieldByField(personRepresentation);
    }

}
