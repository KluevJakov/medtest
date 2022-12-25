package ru.sstu.medtest.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.sstu.medtest.entity.Role;

@Getter
@AllArgsConstructor
@ToString
public class JwtResponse {
    private String token;
    private String login;
    private Role role;
    private Long id;
}
