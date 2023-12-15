package com.istef.demo5questionnairerestapi.controllers;


import com.istef.demo5questionnairerestapi.json.Question;
import com.istef.demo5questionnairerestapi.json.Survey;
import com.istef.demo5questionnairerestapi.services.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;


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
    public ResponseEntity<List<Question>> getAllQuestionsInSurvey(@PathVariable int id) {
        return service.listQuestionsInSurvey(id)
                .map(list -> new ResponseEntity<>(list, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND));
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
        return service.addQuestionToSurvey(id, question)
                .map(integer -> new ResponseEntity<>(integer, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(0, HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Integer> deleteQuestionFromSurvey(@PathVariable int surveyId,
                                                            @PathVariable int questionId) {
        return service.deleteQuestionFromSurvey(surveyId, questionId)
                .map(integer -> new ResponseEntity<>(integer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(0, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Integer> deleteQuestionFromSurvey(@PathVariable int surveyId,
                                                            @PathVariable int questionId,
                                                            @RequestBody Question question) {
       boolean updated =  service.updateQuestionFromSurvey(surveyId, questionId, question);
       return new ResponseEntity<>(updated ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }
}
