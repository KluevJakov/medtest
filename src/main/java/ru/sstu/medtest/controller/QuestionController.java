package ru.sstu.medtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.*;
import ru.sstu.medtest.entity.results.QuestionAnswer;
import ru.sstu.medtest.repository.ExamRepository;
import ru.sstu.medtest.repository.QuestionRepository;
import ru.sstu.medtest.repository.StatRepository;
import ru.sstu.medtest.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    public ExamRepository examRepository;
    @Autowired
    public StatRepository statRepository;

    /*** Метод, возвращающий юзеру все ошибки */
    @GetMapping("/getErrors")
    public ResponseEntity<?> getErrors() {
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
        //log.info("try to fetch errors " + questions);
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

        List<Long> favsId = userQuestions.stream().filter(QuestionAnswer::getFavorite).map(e -> e.getRelatedQuestion().getId()).collect(Collectors.toList());

        List<Question> questions = new ArrayList<>();
        if (userQuestions.size() != 0) {
            questions = questionRepository.findAll()
                    .stream()
                    .filter(e -> favsId.contains(e.getId()))
                    .peek(e -> e.setFavorite(favsId.contains(e.getId())))
                    .collect(Collectors.toList());
        }
        //log.info("try to fetch favs " + questions);
        return ResponseEntity.ok().body(questions);
    }

    /*** Метод, возвращающий юзеру все вопросы (для марафона) */
    @GetMapping("/getMarathon")
    public ResponseEntity<?> getMarathon() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<QuestionAnswer> userQuestions = new ArrayList<>(user.getQuestionsAnswers());

        List<Long> favsId = userQuestions.stream().filter(QuestionAnswer::getFavorite).map(e -> e.getRelatedQuestion().getId()).collect(Collectors.toList());

        List<Question> questions = questionRepository.findAll()
                    .stream()
                    .peek(e -> e.setFavorite(favsId.contains(e.getId())))
                    .collect(Collectors.toList());

        //log.info("try to fetch marathon " + questions);
        Collections.shuffle(questions); //мешаем
        return ResponseEntity.ok().body(questions);
    }

    /*** Метод, возвращающий юзеру вопросы для экзамена */
    @GetMapping("/getExam")
    public ResponseEntity<?> getExam() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Question> questions = questionRepository.findAll();
        Collections.shuffle(questions); //мешаем
        questions = questions.stream().limit(20).collect(Collectors.toList());
        //log.info("try to fetch exam " + questions);
        return ResponseEntity.ok().body(questions);
    }

    @PostMapping("/runExam")
    public ResponseEntity<?> runExam() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (examRepository.findByUserAttempt(user).isPresent()){
            //log.info("Exam session is expired");
            return ResponseEntity.badRequest().body("Session expired");
        }
        try {
            Runnable runnable = () -> {

                    Exam exam = new Exam();
                    exam.setUserAttempt(user);
                    //log.info("Create exam session : " + exam);
                    exam = examRepository.save(exam);
                try {
                    Thread.sleep(1000 * 10); // * 20
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //log.info("Delete exam session : " + exam);
                    examRepository.delete(exam);

            };

            runnable.run();
        } catch (Exception e) {
            //log.error("Something went wrong");
        }

        //log.info("Exam session successfully closed");
        return ResponseEntity.ok().body("");
    }

    /*** Метод, принимающий ответ на билет */
    @PostMapping("/answer")
    public ResponseEntity<?> answer(@RequestBody List<Question> questions) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (examRepository.findByUserAttempt(user).isPresent()){
            //log.info("Exam session is early expired");
            examRepository.delete(examRepository.findByUserAttempt(user).get());

            Stat stat = new Stat();
            stat.setName(user.getName());
            stat.setLastPass(new Date());
            stat.setErrorCount((int)questions.stream().filter(e -> e.getStatus() == QuestionStatus.FALSE).count());
            stat = statRepository.save(stat);
            //log.info("Stat was saved: " + stat);
        }

        for (Question t : questions) {
            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setRelatedQuestion(t);
            questionAnswer.setStatus(t.getStatus());
            questionAnswer.setFavorite(t.getFavorite());
            user.getQuestionsAnswers().removeIf(e -> e.getRelatedQuestion().getId().equals(t.getId()));
            user.getQuestionsAnswers().add(questionAnswer);
        }

        userRepository.save(user);
        //log.info(user.getLogin() + " answered in marathon, errors or favs");
        return ResponseEntity.ok().body("");
    }
}
