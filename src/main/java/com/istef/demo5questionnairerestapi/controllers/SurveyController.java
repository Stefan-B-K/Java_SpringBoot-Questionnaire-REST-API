package com.istef.demo5questionnairerestapi.controllers;


import com.istef.demo5questionnairerestapi.json.Question;
import com.istef.demo5questionnairerestapi.json.Survey;
import com.istef.demo5questionnairerestapi.services.SurveyService;
import com.sun.net.httpserver.Headers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("surveys")
public class SurveyController {

    private final SurveyService service;

    public SurveyController(SurveyService service) {
        this.service = service;
    }

    @GetMapping
    public List<Survey> getAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Survey getSurvey(@PathVariable int id) {
        return service.findSurvey(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/questions")
    public List<Question> getAllQuestionsInSurvey(@PathVariable int id) {
        return service.listQuestionsInSurvey(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{surveyId}/questions/{questionId}")
    public Question getQuestionInSurvey(@PathVariable int surveyId,
                                        @PathVariable int questionId) {
        return service.findQuestionInSurvey(surveyId, questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<Integer> addQuestionToSurvey(@PathVariable int id,
                                                       @RequestBody Question question) {
        Optional<Integer> newId = service.addQuestionToSurvey(id, question);
        return newId.map(integer -> new ResponseEntity<>(integer, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(0, HttpStatus.NOT_FOUND));
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
