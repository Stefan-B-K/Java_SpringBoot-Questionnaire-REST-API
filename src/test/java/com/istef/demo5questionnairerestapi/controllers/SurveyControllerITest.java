package com.istef.demo5questionnairerestapi.controllers;


import com.istef.demo5questionnairerestapi.json.Question;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SurveyControllerITest {

    @Autowired
    private TestRestTemplate template;

    private static String QUESTION_URL = "/surveys/1/questions/1";

    private static String QUESTIONS_URL = "/surveys/1/questions";

    private static String QUESTION_JSON_RESPONSE = """
            {
                "id": 1
            }
            """;
    private static String QUESTIONS_LIST_JSON_RESPONSE = """
            [
                {
                    "id": 1
                },
                {
                    "id": 2
                },
                {
                    "id": 3
                }
            ]
            """;


    private static String NEW_QUESTION_JSON = """
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
    public void getQuestionInSurvey_JSON() throws JSONException {
        ResponseEntity<String> responseEntity = template
                .getForEntity(QUESTION_URL, String.class);

        JSONAssert.assertEquals(QUESTION_JSON_RESPONSE, responseEntity.getBody(), false);
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void getQuestionInSurvey_Object() {
        Question result = template
                .getForObject(QUESTION_URL, Question.class);

        assertEquals(1, result.getId());
        assertFalse(result.getOptions().isEmpty());
        assertTrue(result.getOptions().contains(result.getCorrectAnswer()));
    }

    @Test
    public void getAllQuestionsInSurvey_JSON() throws JSONException {
        ResponseEntity<String> responseEntity = template
                .getForEntity(QUESTIONS_URL, String.class);

        JSONAssert.assertEquals(QUESTIONS_LIST_JSON_RESPONSE, responseEntity.getBody(), false);
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }


    @Test
    public void add_deleteQuestionToSurvey_Json_OK() throws JSONException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(NEW_QUESTION_JSON, httpHeaders);

        ResponseEntity<String> responseEntity = template
                .postForEntity(QUESTIONS_URL, httpEntity, String.class);

        assertEquals("4", responseEntity.getBody());
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        template.delete(QUESTIONS_URL + '/' + 4);
    }

}
