package com.istef.demo5questionnairerestapi.controllers;


import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SurveyControllerFTest {

    @Autowired
    private TestRestTemplate template;

    private final HttpEntity<String> httpEntity = new HttpEntity<>(null, setHeaders());

    private static final String QUESTION_URL = "/surveys/1/questions/1";

    private static final String QUESTIONS_URL = "/surveys/1/questions";

    private static final String QUESTION_JSON_RESPONSE = """
            {
                "id": 1
            }
            """;
    private static final String QUESTIONS_LIST_JSON_RESPONSE = """
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
    public void getQuestionInSurvey_JSON() throws JSONException {
        ResponseEntity<String> responseEntity = template
                .exchange(QUESTION_URL, HttpMethod.GET, httpEntity, String.class);

        JSONAssert.assertEquals(QUESTION_JSON_RESPONSE, responseEntity.getBody(), false);
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void getAllQuestionsInSurvey_JSON() throws JSONException {
        ResponseEntity<String> responseEntity = template
                .exchange(QUESTIONS_URL, HttpMethod.GET, httpEntity, String.class);
        JSONAssert.assertEquals(QUESTIONS_LIST_JSON_RESPONSE, responseEntity.getBody(), false);
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }


    @Test
    public void add_deleteQuestionToSurvey_Json_OK() {
        HttpEntity<String> httpEntity = new HttpEntity<>(NEW_QUESTION_JSON, setHeaders());
        ResponseEntity<String> responseEntityPost = template
                .postForEntity(QUESTIONS_URL, httpEntity, String.class);

        assertEquals("4", responseEntityPost.getBody());
        assertEquals("application/json", responseEntityPost.getHeaders().get("Content-Type").get(0));
        assertTrue(responseEntityPost.getStatusCode().is2xxSuccessful());

        ResponseEntity<String> responseEntityDelete = template
                .exchange(QUESTIONS_URL + '/' + 4, HttpMethod.DELETE, httpEntity, String.class);
        assertTrue(responseEntityDelete.getStatusCode().is2xxSuccessful());
    }

    private HttpHeaders setHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", "Basic " + basicAuthEncode("Stef", "123"));
        return httpHeaders;
    }

    private String basicAuthEncode(String user, String password) {
        String combined = user + ':' + password;
        byte[] bytes = Base64.getEncoder().encode(combined.getBytes());
        return new String(bytes);
    }

}
