package ru.sstu.medtest.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {
    private Long userId;
    private String login;
    private String group;
    private Long roleId;
}
