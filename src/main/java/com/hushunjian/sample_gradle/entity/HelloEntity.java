package com.hushunjian.sample_gradle.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hello")
public class HelloEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "message", columnDefinition = "varchar(255) not null default '' comment '信息'")
    private String message;
}
