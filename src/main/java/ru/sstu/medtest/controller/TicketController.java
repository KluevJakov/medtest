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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TicketAnswer> userTickets = new ArrayList<>(user.getTicketsAnswers());
        List<QuestionAnswer> userQuestions = new ArrayList<>(user.getQuestionsAnswers());

        List<Ticket> tickets = ticketRepository.findAll().stream()
                .peek(e -> {
                    if (userTickets.stream().anyMatch(x -> x.getRelatedTicket().getId().equals(e.getId()))) {
                        TicketAnswer current = userTickets.stream().filter(x -> x.getRelatedTicket().getId().equals(e.getId())).findFirst().get();
                        e.setStatus(current.getStatus());
                        e.setLastPass(current.getLastPass());
                        e.setErrorCount(current.getErrorCount());

                        Set<Question> questions = e.getQuestions()
                                .stream().peek(x -> x.setFavorite(userQuestions.stream()
                                        .anyMatch(z -> z.getRelatedQuestion().getId().equals(x.getId())) ? userQuestions.stream()
                                        .filter(z -> z.getRelatedQuestion().getId().equals(x.getId()))
                                        .findFirst().get()
                                        .getFavorite() : false))
                                .collect(Collectors.toSet());

                        e.setQuestions(questions);
                    } else {
                        e.setStatus(QuestionStatus.NOTANSWERED);
                    }
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tickets);
    }

    /*** Метод, принимающий ответ на билет */
    @PostMapping("/answer")
    public ResponseEntity<?> answer(@RequestBody Ticket ticket) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TicketAnswer ticketAnswer = new TicketAnswer();
        ticketAnswer.setRelatedTicket(ticket);
        if (ticket.getQuestions().stream().anyMatch(e -> e.getStatus().equals(QuestionStatus.FALSE))) {
            ticketAnswer.setStatus(QuestionStatus.FALSE);
            ticketAnswer.setErrorCount((int) ticket.getQuestions().stream().filter(e -> e.getStatus().equals(QuestionStatus.FALSE)).count());
        } else {
            ticketAnswer.setErrorCount(0);
            ticketAnswer.setStatus(QuestionStatus.TRUE);
        }
        ticketAnswer.setLastPass(new Date());

        user.getTicketsAnswers().add(ticketAnswer);

        for (Question t : ticket.getQuestions()) {
            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setRelatedQuestion(t);
            questionAnswer.setStatus(t.getStatus());
            questionAnswer.setFavorite(t.getFavorite());
            user.getQuestionsAnswers().removeIf(e -> e.getRelatedQuestion().getId().equals(t.getId()));
            user.getQuestionsAnswers().add(questionAnswer);
        }

        userRepository.save(user);
        return ResponseEntity.ok().body("");
    }

    @PostMapping("/create")
    public ResponseEntity<?> create() {
        Ticket ticket = new Ticket();
        ticket.setStatus(QuestionStatus.NOTANSWERED);
        ticketRepository.save(ticket);
        return ResponseEntity.ok().body("");
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Ticket ticket = ticketRepository.getById(id);
        ticket.getQuestions().forEach(e -> e.getAnswers().forEach(j -> questionAnswerRepository.removeLinks(j.getId())));
        ticket.getQuestions().forEach(e -> themeRepository.removeLinks(e.getId()));
        ticket.getQuestions().forEach(e -> questionAnswerRepository.removeLinks1(e.getId()));
        ticket.getQuestions().forEach(e -> questionAnswerRepository.removeLinks2(e.getId()));
        ticketAnswerRepository.removeLinks(id);
        ticketRepository.removeLinks(id);
        ticketRepository.deleteById(id);
        return ResponseEntity.ok().body("");
    }
}
