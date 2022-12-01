package ru.sstu.medtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.repository.ThemeRepository;

@RestController
@RequestMapping("/api/theme")
@CrossOrigin(origins = "http://localhost:4200")
public class ThemeController {

    @Autowired
    public ThemeRepository themeRepository;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(themeRepository.findAll());
        return ResponseEntity.ok().body(themeRepository.findAll());
    }
}
