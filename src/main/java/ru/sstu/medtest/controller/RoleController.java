package ru.sstu.medtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sstu.medtest.entity.Role;
import ru.sstu.medtest.repository.RoleRepository;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/all")
    public List<Role> all() {
        return roleRepository.findAll();
    }
}
