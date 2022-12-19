package ru.sstu.medtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.Question;
import ru.sstu.medtest.entity.QuestionStatus;
import ru.sstu.medtest.entity.Ticket;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.entity.results.QuestionAnswer;
import ru.sstu.medtest.entity.results.TicketAnswer;
import ru.sstu.medtest.repository.QuestionRepository;
import ru.sstu.medtest.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/question")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class QuestionController {
    @Autowired
    public QuestionRepository questionRepository;
    @Autowired
    public UserRepository userRepository;

    /*** Метод, возвращающий юзеру все ошибки */
    @GetMapping("/getErrors")
    public ResponseEntity<?> getAll() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //текуший юзер

        List<QuestionAnswer> userQuestions = user.getQuestionsAnswers() //получаем список пройденных вопросов
                .stream()
                .filter(e -> e.getStatus().equals(QuestionStatus.FALSE)) //но только отвеченные неверно
                .collect(Collectors.toList());

        List<Long> errorsId = userQuestions.stream().map(e -> e.getRelatedQuestion().getId()).collect(Collectors.toList()); //а точнее их id
        List<Long> favsId = userQuestions.stream().filter(QuestionAnswer::getFavorite).map(e -> e.getRelatedQuestion().getId()).collect(Collectors.toList());

        List<Question> questions = new ArrayList<>();
        if (userQuestions.size() != 0) {
            questions = questionRepository.findAll()
                    .stream()
                    .filter(e -> errorsId.contains(e.getId())) //возвращаем оригинальные вопросы, совпавшие с отвеченными с ошибками
                    .peek(e -> e.setFavorite(favsId.contains(e.getId()))) //но выставяем им маркеры избранности
                    .collect(Collectors.toList());
        }
        log.info("try to fetch errors " + questions);
        return ResponseEntity.ok().body(questions);
    }

    /*** Метод, возвращающий юзеру избранные вопросы */
    @GetMapping("/getFavorite")
    public ResponseEntity<?> getFavorite() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<QuestionAnswer> userQuestions = user.getQuestionsAnswers()
                .stream()
                .filter(e -> e.getFavorite().equals(true))
                .collect(Collectors.toList());

        List<Long> favsId = userQuestions.stream().map(e -> e.getRelatedQuestion().getId()).collect(Collectors.toList());

        List<Question> questions = new ArrayList<>();
        if (userQuestions.size() != 0) {
            questions = questionRepository.findAll()
                    .stream()
                    .filter(e -> favsId.contains(e.getId()))
                    .peek(e -> e.setFavorite(favsId.contains(e.getId())))
                    .collect(Collectors.toList());
        }
        log.info("try to fetch favs " + questions);
        return ResponseEntity.ok().body(questions);
    }

    /*** Метод, возвращающий юзеру все вопросы (для марафона) */
    @GetMapping("/getMarathon")
    public ResponseEntity<?> getMarathon() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<QuestionAnswer> userQuestions = new ArrayList<>(user.getQuestionsAnswers());

        List<Long> favsId = userQuestions.stream().map(e -> e.getRelatedQuestion().getId()).collect(Collectors.toList());

        System.out.println(favsId);
        List<Question> questions = new ArrayList<>();
        if (userQuestions.size() != 0) {
            questions = questionRepository.findAll()
                    .stream()
                    .peek(e -> e.setFavorite(favsId.contains(e.getId())))
                    .collect(Collectors.toList());
        }
        log.info("try to fetch marathon " + questions);
        return ResponseEntity.ok().body(questions);
    }

    /*** Метод, принимающий ответ на билет */
    @PostMapping("/answer")
    public ResponseEntity<?> answer(@RequestBody List<Question> questions) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        for (Question t : questions) {
            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setRelatedQuestion(t);
            questionAnswer.setStatus(t.getStatus());
            questionAnswer.setFavorite(t.getFavorite());
            user.getQuestionsAnswers().removeIf(e -> e.getRelatedQuestion().getId().equals(t.getId()));
            user.getQuestionsAnswers().add(questionAnswer);
        }

        userRepository.save(user);
        log.info(user.getLogin() + " answered in marathon, errors or favs");
        return ResponseEntity.ok().body("");
    }
}
