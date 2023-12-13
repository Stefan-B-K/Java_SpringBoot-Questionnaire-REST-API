package com.istef.demo5questionnairerestapi.services;

import com.istef.demo5questionnairerestapi.json.Question;
import com.istef.demo5questionnairerestapi.json.Survey;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class SurveyService {

    private static List<Survey> surveys = new ArrayList<>();
    private static Integer count = 0;
    static {
        Question question1 = new Question(++count,
                "Most Popular Cloud Platform Today", Arrays.asList(
                "AWS", "Azure", "Google Cloud", "Oracle Cloud"),
                "AWS");
        Question question2 = new Question(++count,
                "Fastest Growing Cloud Platform", Arrays.asList(
                "AWS", "Azure", "Google Cloud", "Oracle Cloud"),
                "Google Cloud");
        Question question3 = new Question(++count,
                "Most Popular DevOps Tool", Arrays.asList(
                "Kubernetes", "Docker", "Terraform", "Azure DevOps"),
                "Kubernetes");

        List<Question> questions = new ArrayList<>(Arrays.asList(question1,
                question2, question3));

        Survey survey = new Survey(1, "My Favorite Survey",
                "Description of the Survey", questions);

        surveys.add(survey);
    }

    public List<Survey> listAll() {
        return surveys;
    }

    public Optional<Survey> findSurvey(int id) {
        return surveys.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public Optional<List<Question>> listQuestionsInSurvey(int id) {
        return findSurvey(id).map(Survey::getQuestions);
    }

    public Optional<Question> findQuestionInSurvey(int surveyId, int questionId) {
        Optional<List<Question>> questions = findSurvey(surveyId).map(Survey::getQuestions);
        return questions.map(questionList -> questionList.stream()
                        .filter(s -> s.getId().equals(questionId))
                        .findFirst())
                .orElse(null);
    }

    public Optional<Integer> addQuestionToSurvey(int surveyId, Question question) {
        Optional<List<Question>> questions = listQuestionsInSurvey(surveyId);
        if (questions.isEmpty()) return Optional.empty();
        question.setId(++count);
        questions.get().add(question);
        return Optional.of(count);
    }
}
