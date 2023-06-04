package ru.sstu.medtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sstu.medtest.entity.Role;
import ru.sstu.medtest.entity.UserEntity;
import ru.sstu.medtest.entity.dto.UserDto;
import ru.sstu.medtest.repository.RoleRepository;
import ru.sstu.medtest.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/all")
    public List<UserEntity> allUsers(@RequestParam(required = false) String login,
                                     @RequestParam(required = false) String group,
                                     @RequestParam(required = false) String role,
                                     @RequestParam(required = false) Integer size) {
        if (size == null) {
            size = 20;
        }
        if (login == null) {
            login = "";
        }
        if (group == null) {
            group = "";
        }
        if (role == null) {
            role = "";
        }

        String finalLogin = login;
        String finalGroup = group;
        String finalRole = role;
        return userRepository.findAll().stream()
                .filter(e -> !e.getRoles().contains(roleRepository.findBySystemName("ADMIN")))
                .filter(e -> e.getLogin().toLowerCase().contains(finalLogin.toLowerCase()))
                .filter(e -> e.getGroupp().toLowerCase().contains(finalGroup.toLowerCase()))
                .filter(e -> e.getRoles().stream().anyMatch(j -> j.getDisplayName().toLowerCase().contains(finalRole.toLowerCase())))
                .limit(size)
                .collect(Collectors.toList());
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserDto userDto) {
        UserEntity userEntity = userRepository.findById(userDto.getUserId()).get();
        userEntity.setLogin(userDto.getLogin());
        userEntity.setGroupp(userDto.getGroup());
        Role role = roleRepository.findById(userDto.getRoleId()).get();
        userEntity.setRoles(new HashSet<>());
        userEntity.getRoles().add(role);
        userRepository.save(userEntity);
        return ResponseEntity.ok("");
    }
}
