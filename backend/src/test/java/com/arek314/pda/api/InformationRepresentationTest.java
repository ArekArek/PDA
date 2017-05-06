package com.arek314.pda.api;

import com.arek314.pda.db.model.Information;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class InformationRepresentationTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private final int FIXTURE_INFORMATION_ID = 3;
    private final String FIXTURE_INFORMATION_MAP_URL = "http://www.example.com/sample.png";
    private InformationRepresentation informationRepresentation;
    private Information informationModel;

    @Before
    public void setup() {
        informationRepresentation = new InformationRepresentation(FIXTURE_INFORMATION_ID, FIXTURE_INFORMATION_MAP_URL);
    }

    @Test
    public void serializeToJSON() throws Exception {
        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/informationRepresentation.json"), InformationRepresentation.class));

        assertThat(MAPPER.writeValueAsString(informationRepresentation)).isEqualTo(expected);
    }

    @Test
    public void deserializeFromJSON() throws Exception {
        final InformationRepresentation representation = MAPPER.readValue(fixture("fixtures/informationRepresentation" + ".json"), InformationRepresentation.class);

        assertThat(representation).isEqualToComparingFieldByField(informationRepresentation);
    }
}
