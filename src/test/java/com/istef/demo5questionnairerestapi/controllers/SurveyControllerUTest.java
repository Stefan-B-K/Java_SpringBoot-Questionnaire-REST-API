package com.istef.demo5questionnairerestapi.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.istef.demo5questionnairerestapi.json.Question;
import com.istef.demo5questionnairerestapi.services.SurveyService;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;


@WebMvcTest(SurveyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SurveyControllerUTest {

    @MockBean
    private SurveyService surveyService;

    @Autowired
    private MockMvc mockMvc;

    private static final String QUESTION_URL = "http://localhost:8081/surveys/1/questions/1";
    private static final String QUESTIONS_URL = "http://localhost:8081/surveys/1/questions";

    private static final Question question = new Question(1, "Most Popular Cloud Platform Today",
            Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"),
            "AWS");

    private static final String schemaJson = """
                {
                    "title": "Question",
                    "type": "object",
                    "properties": {
                        "id": { "type": "integer" },
                        "text": { "type": "string" },
                        "options": {
                            "type": "array",
                            "items": {"type": "string"},
                            "minItems": 2
                        },
                        "correctAnswer": { "type": "string"}
                    },
                    "required": ["id", "text", "options", "correctAnswer"]
                }
                """;
    private static final String NEW_QUESTION_JSON = """
            {
                "text": "What ... ?",
                "options": [
                    "me",
                    "you",
                    "her",
                    "them"
                ],
                "correctAnswer": "me"
            }
            """;

    @Test
    public void getQuestionInSurvey() throws Exception {
        when(surveyService.findQuestionInSurvey(1, 1))
                .thenReturn(Optional.of(question));

        MockHttpServletResponse response = mockMvc
                .perform(get(QUESTION_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = factory.getSchema(schemaJson);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode question = objectMapper.readTree(response.getContentAsString());
        Set<ValidationMessage> errors = jsonSchema.validate(question);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void addQuestionToSurvey() throws Exception {
        when(surveyService.addQuestionToSurvey(anyInt(), any()))
                .thenReturn(Optional.of(111));

        MockHttpServletResponse response = mockMvc.perform(post(QUESTIONS_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(NEW_QUESTION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        assertEquals("111", response.getContentAsString());
    }
}
