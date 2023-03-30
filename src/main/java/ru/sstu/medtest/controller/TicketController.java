package ru.sstu.medtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.Question;
import ru.sstu.medtest.entity.QuestionStatus;
import ru.sstu.medtest.entity.Ticket;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.entity.results.QuestionAnswer;
import ru.sstu.medtest.entity.results.TicketAnswer;
import ru.sstu.medtest.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TicketController {
    @Autowired
    public TicketRepository ticketRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public TicketAnswerRepository ticketAnswerRepository;
    @Autowired
    public QuestionAnswerRepository questionAnswerRepository;

    /*** Метод формирующий для пользователя список изученных и неизученных тем | D: */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //получаем текущего юзера

        List<TicketAnswer> userTickets = new ArrayList<>(user.getTicketsAnswers()); //получаем все пройденные билеты юзера
        List<QuestionAnswer> userQuestions = new ArrayList<>(user.getQuestionsAnswers()); //получаем все пройденные (и правильные и неправильные) вопросы юзера

        List<Ticket> tickets = ticketRepository.findAll().stream() //формируем билеты на выдачу
                .map(e -> {
                    if (userTickets.stream().anyMatch(x -> x.getRelatedTicket().getId().equals(e.getId()))) { //если билет был когда-то пройден
                        TicketAnswer current = userTickets.stream().filter(x -> x.getRelatedTicket().getId().equals(e.getId())).findFirst().get(); //находим этот пройденный билет
                        e.setStatus(current.getStatus()); //выставляем статус TRUE или FALSE
                        e.setLastPass(current.getLastPass()); //выставляем дату последнего прохождения
                        e.setErrorCount(current.getErrorCount()); //выставляем кол-во ошибок последнего прохождения

                        Set<Question> questions = e.getQuestions() //формируем инфу о пройденных вопросов, (на самом деле нас интересуют только поле favorite)
                                .stream().map(x -> {
                                    x.setFavorite(userQuestions.stream() //выставляем маркер избранного вопроса, если найден в пройденных
                                            .anyMatch(z -> z.getRelatedQuestion().getId().equals(x.getId())) ? userQuestions.stream()
                                            .filter(z -> z.getRelatedQuestion().getId().equals(x.getId()))
                                            .findFirst().get()
                                            .getFavorite() : false);
                                    return x;
                                })
                                .collect(Collectors.toSet());

                        e.setQuestions(questions); //выставляем отредаченые вопросы
                    } else {
                        e.setStatus(QuestionStatus.NOTANSWERED); //если билет никогда не был пройден, выставляем статус NOTANSWERED
                    }
                    return e;
                })
                .collect(Collectors.toList());
        log.info(tickets.toString());
        return ResponseEntity.ok().body(tickets);
    }

    /*** Метод, принимающий ответ на билет */
    @PostMapping("/answer")
    public ResponseEntity<?> answer(@RequestBody Ticket ticket) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //получаем текущего юзера

        TicketAnswer ticketAnswer = new TicketAnswer(); //создаем модель отвеченного билета
        ticketAnswer.setRelatedTicket(ticket); //привязываем его к оригинальному билету
        if (ticket.getQuestions().stream().anyMatch(e -> e.getStatus().equals(QuestionStatus.FALSE))) { //выставляем статус и кол-во ошибок
            ticketAnswer.setStatus(QuestionStatus.FALSE);
            ticketAnswer.setErrorCount((int)ticket.getQuestions().stream().filter(e -> e.getStatus().equals(QuestionStatus.FALSE)).count());
        } else {
            ticketAnswer.setErrorCount(0);
            ticketAnswer.setStatus(QuestionStatus.TRUE);
        }
        ticketAnswer.setLastPass(new Date()); //выставялем дату последнего выполнения (текущую)

        log.info("" + user.getTicketsAnswers().removeIf(e -> e.getRelatedTicket().getId().equals(ticketAnswer.getRelatedTicket().getId())));
        user.getTicketsAnswers().add(ticketAnswer); //заменяем билет, если он уже был когда-то пройден
        log.info(user.getTicketsAnswers().toString());

        for (Question t : ticket.getQuestions()) { // пробегаемся по отвеченным вопросам
            QuestionAnswer questionAnswer = new QuestionAnswer(); //создаем модель отвеченного вопроса
            questionAnswer.setRelatedQuestion(t); //привязываем к оригинальному вопросу
            questionAnswer.setStatus(t.getStatus()); //выставляем статус TRUE или FALSE
            questionAnswer.setFavorite(t.getFavorite()); //выставляем маркер избранности
            user.getQuestionsAnswers().removeIf(e -> e.getRelatedQuestion().getId().equals(t.getId()));
            user.getQuestionsAnswers().add(questionAnswer); //заменяем вопрос, если он уже был когда-то пройден
        }

        userRepository.save(user); //все обработанное выше сохраняем в бд
        log.info(user.getLogin() + " answered " + user.getTicketsAnswers());
        return ResponseEntity.ok().body("");
    }

    @PostMapping("/create")
    public ResponseEntity<?> create() {
        Ticket ticket = new Ticket();
        ticket.setStatus(QuestionStatus.NOTANSWERED);
        Ticket ready = ticketRepository.save(ticket);
        return ResponseEntity.ok().body("");
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Ticket ticket = ticketRepository.getById(id);
        ticket.getQuestions().stream()
                .forEach(e -> e.getAnswers().stream()
                        .forEach(j -> questionAnswerRepository.removeLinks(j.getId())));
        ticket.getQuestions().stream()
                .forEach(e -> themeRepository.removeLinks(e.getId()));
        ticket.getQuestions().stream()
                .forEach(e -> questionAnswerRepository.removeLinks1(e.getId()));
        ticket.getQuestions().stream()
                .forEach(e -> questionAnswerRepository.removeLinks2(e.getId()));
        ticketAnswerRepository.removeLinks(id);
        ticketRepository.removeLinks(id);
        ticketRepository.deleteById(id);
        return ResponseEntity.ok().body("");
    }
}
