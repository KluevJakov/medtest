package ru.sstu.medtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.Theme;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.repository.ThemeRepository;
import ru.sstu.medtest.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/theme")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ThemeController {
    @Autowired
    public ThemeRepository themeRepository;
    @Autowired
    public UserRepository userRepository;

    /*** Метод формирующий для пользователя список изученных и неизученных тем */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Theme> themes = themeRepository.findAll().stream()
                .peek(e -> e.setLearned(user.getThemes().stream().anyMatch(x -> x.getId().equals(e.getId()))))
                .collect(Collectors.toList());
        //log.info(themes.toString());
        return ResponseEntity.ok().body(themes);
    }

    /*** Метод помечающий для пользователя тему как изученную */
    @GetMapping("/learnTheme/{id}")
    public ResponseEntity<?> learnTheme(@PathVariable Long id) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Theme theme = themeRepository.findById(id).get();
        if (user.getThemes().stream().noneMatch(x -> x.getId().equals(theme.getId()))) {
            user.getThemes().add(theme);
            userRepository.save(user);
            //log.info(user.getLogin() + " learned " + theme.getTitle());
        } else {
            //log.info(user.getLogin() + " already learned this " + theme.getTitle());
        }
        return ResponseEntity.ok().body("");
    }

    /*** Метод помечающий для пользователя тему как неизученную */
    @GetMapping("/unlearnTheme/{id}")
    public ResponseEntity<?> unlearnTheme(@PathVariable Long id) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Theme theme = themeRepository.findById(id).get();
        if (user.getThemes().stream().anyMatch(x -> x.getId().equals(theme.getId()))) {
            user.setThemes(user.getThemes().stream().filter(e -> !e.getId().equals(theme.getId())).collect(Collectors.toList()));
            userRepository.save(user);
            //log.info(user.getLogin() + " unlearned " + theme.getTitle());
        } else {
            //log.info(user.getLogin() + " already unlearned this " + theme.getTitle());
        }
        return ResponseEntity.ok().body("");
    }
}
