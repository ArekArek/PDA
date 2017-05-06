package com.arek314.pda.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageRepresentationTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private final int FIXTURE_MESSAGE_ID = 2;
    private final int FIXTURE_MESSAGE_USER_ID = 111110;
    private final Timestamp FIXTURE_MESSAGE_DATE = new Timestamp(1488559854000L);
    private final String FIXTURE_MESSAGE_SENDER = "tester";
    private final String FIXTURE_MESSAGE_MESSAGE = "test foo bar iwg";
    private MessageRepresentation messageRepresentation;

    @Before
    public void setup() {
        messageRepresentation = new MessageRepresentation(FIXTURE_MESSAGE_ID, FIXTURE_MESSAGE_USER_ID, FIXTURE_MESSAGE_DATE, FIXTURE_MESSAGE_SENDER, FIXTURE_MESSAGE_MESSAGE);
    }

    @Test
    public void serializeToJSON() throws Exception {
        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/messageRepresentation.json"), MessageRepresentation.class));

        assertThat(MAPPER.writeValueAsString(messageRepresentation)).isEqualTo(expected);
    }

    @Test
    public void deserializeFromJSON() throws Exception {
        final MessageRepresentation representation = MAPPER.readValue(fixture("fixtures/messageRepresentation" + ".json"), MessageRepresentation.class);

        assertThat(representation).isEqualToComparingFieldByField(messageRepresentation);
    }
}
