package ru.sstu.medtest.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class JwtResponse {
    private String token;
    private String login;
    private String role;
    private Long id;
}
