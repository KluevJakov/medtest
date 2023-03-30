package ru.sstu.medtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.Answer;
import ru.sstu.medtest.entity.Question;
import ru.sstu.medtest.entity.QuestionStatus;
import ru.sstu.medtest.entity.Ticket;
import ru.sstu.medtest.repository.AnswerRepository;
import ru.sstu.medtest.repository.QuestionAnswerRepository;
import ru.sstu.medtest.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
@RequestMapping("/api/answer")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class AnswerController {

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    public QuestionAnswerRepository questionAnswerRepository;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Answer answer) {
        Long relatedQuestionId = answer.getId();

        System.out.println(answer);
        answer.setId(null);
        Answer ready = answerRepository.save(answer);

        Question question = questionRepository.getById(relatedQuestionId);

        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<>());
        }

        question.getAnswers().add(ready);

        questionRepository.save(question);

        return ResponseEntity.ok().body("");
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        questionAnswerRepository.removeLinks(id);
        answerRepository.deleteById(id);
        return ResponseEntity.ok().body("");
    }
}
