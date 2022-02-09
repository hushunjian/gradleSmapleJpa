package com.hushunjian.sample_gradle.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "folder")
public class FolderEntity extends TreeEntity {

    @Column(name = "name", columnDefinition = "varchar(200) not null default '' comment '文件夹名称'")
    private String name;
}
