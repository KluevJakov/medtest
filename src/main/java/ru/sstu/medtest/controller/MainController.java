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
import ru.sstu.medtest.entity.Role;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.entity.auth.JwtRequest;
import ru.sstu.medtest.entity.auth.JwtResponse;
import ru.sstu.medtest.repository.RoleRepository;
import ru.sstu.medtest.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;

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
    PasswordEncoder encoder;

    @Autowired
    JwtTokenUtil jwtUtils;

    @GetMapping("")
    public String welcome() {
        return "home";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest user) {
        System.out.println(user);
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
        System.out.println("generateJwtToken " + jwt);
        return ResponseEntity.ok(new JwtResponse(jwt, userAttempt.get().getLogin(), userAttempt.get().getRoles().toString(), userAttempt.get().getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {

        System.out.println(user);
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

        user.setPassword(encoder.encode(user.getPassword()));
        user.setPasswordAccept(null);
        Role regRole = roleRepository.findBySystemName("USER");
        user.setRoles(new HashSet<>());
        user.getRoles().add(regRole);
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok("");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(user);
    }


}
