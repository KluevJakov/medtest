package ru.sstu.medtest.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private Integer estimatedTime;
    @Column(length = 4096)
    private String text;
    private Boolean learned;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Question> questions;
}
