package ru.sstu.medtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.*;
import ru.sstu.medtest.entity.results.QuestionAnswer;
import ru.sstu.medtest.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    public QuestionAnswerRepository questionAnswerRepository;

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
                .limit(20)
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

        if (examRepository.findByUserAttempt(user).isPresent()) {
            return ResponseEntity.badRequest().body("Session expired");
        }
        try {
            Runnable runnable = () -> {
                Exam exam = new Exam();
                exam.setDate(LocalDateTime.now());
                exam.setUserAttempt(user);
                exam = examRepository.save(exam);
                try {
                    Thread.sleep(1000 * 25); // * 20
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                examRepository.delete(exam);
            };
            runnable.run();
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok().body("");
    }

    /*** Метод, принимающий ответ на билет */
    @PostMapping("/answer")
    public ResponseEntity<?> answer(@RequestBody List<Question> questions) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Exam> exam = examRepository.findByUserAttempt(user);
        if (exam.isPresent()) {
            Stat stat = new Stat();
            stat.setName(user.getLogin());
            stat.setGroupp(user.getGroupp());
            stat.setLastPass(ChronoUnit.SECONDS.between(exam.get().getDate(), LocalDateTime.now()));
            stat.setPassDate(new Date());
            examRepository.delete(examRepository.findByUserAttempt(user).get());
            stat.setErrorCount((int) questions.stream().filter(e -> e.getStatus() == QuestionStatus.FALSE).count());
            stat = statRepository.save(stat);
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

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Question question) {
        Long relatedTicketId = question.getId();

        question.setStatus(QuestionStatus.NOTANSWERED);
        question.setFavorite(false);
        question.setId(null);
        Question ready = questionRepository.save(question);

        Ticket ticket = ticketRepository.getById(relatedTicketId);

        if (ticket.getQuestions() == null) {
            ticket.setQuestions(new HashSet<>());
        }

        ticket.getQuestions().add(ready);

        ticketRepository.save(ticket);

        return ResponseEntity.ok().body(ready);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Question question = questionRepository.getById(id);
        questionAnswerRepository.removeLinks1(id);
        questionAnswerRepository.removeLinks2(id);
        question.getAnswers().forEach(e -> questionAnswerRepository.removeLinks(e.getId()));
        questionRepository.removeLinks(id);
        themeRepository.removeLinks(id);
        questionRepository.deleteById(id);
        return ResponseEntity.ok().body("");
    }
}
