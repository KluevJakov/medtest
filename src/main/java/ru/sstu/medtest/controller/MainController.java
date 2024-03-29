package ru.sstu.medtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.config.jwt.JwtTokenUtil;
import ru.sstu.medtest.entity.Stat;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.entity.auth.JwtRequest;
import ru.sstu.medtest.entity.auth.JwtResponse;
import ru.sstu.medtest.entity.dto.TestAnswersDto;
import ru.sstu.medtest.repository.RoleRepository;
import ru.sstu.medtest.repository.StatRepository;
import ru.sstu.medtest.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class MainController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    StatRepository statRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtTokenUtil jwtUtils;

    @GetMapping("")
    public String welcome() {
        return "home";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest user) {
        try {
            Optional<UserEntity> userAttempt = userRepository.findByLogin(user.getLogin());
            if (userAttempt.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Ошибка! Пользователь не найден");
            } else if (!encoder.matches(user.getPassword(), userAttempt.get().getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body("Ошибка! Неверный пароль");
            }

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok().body(new JwtResponse(jwt, userAttempt.get().getLogin(), userAttempt.get().getRoles().stream().findFirst().get(), userAttempt.get().getId()));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Что-то пошло не так! Обратитесь к администратору");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        try {
            if (userRepository.findByLogin(user.getLogin()).isPresent()) {
                return ResponseEntity
                        .badRequest()
                        .body("Ошибка! Данный логин уже используется");
            }

            if (!user.getPassword().equals(user.getPasswordAccept())) {
                return ResponseEntity
                        .badRequest()
                        .body("Ошибка! Пароли не совпадают");
            }

            System.out.println(userRepository.count());
            user.setId(userRepository.count() + 1);
            user.setPassword(encoder.encode(user.getPassword()));
            user.setPasswordAccept(null);
            user.setGroupp("");
            user.setRoles(Collections.singleton(roleRepository.findBySystemName("USER")));
            user.setActive(true);

            System.out.println(user);
            var instance = userRepository.save(user);
            System.out.println(instance);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Произошла ошибка при регистрации! Обратитесь к администратору");
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/stat")
    public ResponseEntity<?> stat(@RequestParam(required = false) String login,
                                  @RequestParam(required = false) String group,
                                  @RequestParam(required = false) String date,
                                  @RequestParam(required = false) Integer size,
                                  @RequestParam(required = false) Integer error) {
        String finalDate;
        if (size == null) {
            size = 20;
        }
        if (login == null) {
            login = "";
        }
        if (group == null) {
            group = "";
        }
        if (date == null) {
            Date currentDate = new Date();
            finalDate =  (currentDate.getYear()+1900)+"-";
            finalDate += ((currentDate.getMonth()+1) < 10 ? "0" : "") + (currentDate.getMonth()+1) + "-";
            finalDate += (currentDate.getDate() < 10 ? "0" : "") + currentDate.getDate();
        } else {
            finalDate = date;
        }

        String finalLogin = login;
        String finalGroup = group;
        String finalDate1 = finalDate;

        List<Stat> stats = statRepository.findAll().stream()
                .filter(e -> e.getName().contains(finalLogin))
                .filter(e -> e.getGroupp().contains(finalGroup))
                .filter(e -> e.getPassDate().toString().substring(0, 10).equals(finalDate1))
                .sorted(Comparator.comparing(Stat::getPassDate).reversed())
                .limit(size)
                .collect(Collectors.toList());

        if (error != null) {
            stats = stats.stream().filter(e -> e.getErrorCount().equals(error)).collect(Collectors.toList());
        }

        return ResponseEntity.ok().body(stats);
    }


    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody TestAnswersDto testAnswersDto){
        String result = "";
        int score = 0;

        TestAnswersDto check = TestAnswersDto.builder()
                .q1("а").q2("6").q3("9").q4("7").q5("1").q6("б").q7("в").q8("2")
                .q9("5").q10("3").q11("8").q12("4")
                .build();

        if (testAnswersDto.getQ1().equalsIgnoreCase(check.getQ1())) {
            score += 1;
        }
        if (testAnswersDto.getQ2().equalsIgnoreCase(check.getQ2())) {
            score += 1;
        }
        if (testAnswersDto.getQ3().equalsIgnoreCase(check.getQ3())) {
            score += 1;
        }
        if (testAnswersDto.getQ4().equalsIgnoreCase(check.getQ4())) {
            score += 1;
        }
        if (testAnswersDto.getQ5().equalsIgnoreCase(check.getQ5())) {
            score += 1;
        }
        if (testAnswersDto.getQ6().equalsIgnoreCase(check.getQ6())) {
            score += 1;
        }
        if (testAnswersDto.getQ7().equalsIgnoreCase(check.getQ7())) {
            score += 1;
        }
        if (testAnswersDto.getQ8().equalsIgnoreCase(check.getQ8())) {
            score += 1;
        }
        if (testAnswersDto.getQ9().equalsIgnoreCase(check.getQ9())) {
            score += 1;
        }
        if (testAnswersDto.getQ10().equalsIgnoreCase(check.getQ10())) {
            score += 1;
        }
        if (testAnswersDto.getQ11().equalsIgnoreCase(check.getQ11())) {
            score += 1;
        }
        if (testAnswersDto.getQ12().equalsIgnoreCase(check.getQ12())) {
            score += 1;
        }

        return ResponseEntity.ok("{ \"text\" : \"Правильных ответов "+score+"/12\"}");
    }
}
