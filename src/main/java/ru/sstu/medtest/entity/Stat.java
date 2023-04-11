package ru.sstu.medtest.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String groupp;
    private Integer errorCount;
    private Long lastPass;
    private Date passDate;
}
