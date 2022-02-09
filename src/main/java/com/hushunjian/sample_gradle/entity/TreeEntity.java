package com.hushunjian.sample_gradle.entity;

import com.hushunjian.sample_gradle.util.OutLineUtil;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
public class TreeEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "parent_id", columnDefinition = "varchar(50) not null default '' comment '父节点id'")
    private String parentId;

    /**
     * 树形out_line编号
     */
    @Column(name = "out_line", columnDefinition = "varchar(500) not null default '' comment '树形outLine'")
    private String outLine;

    public int step() {
        return OutLineUtil.STEP;
    }

    public int digit() {
        return OutLineUtil.DIGIT;
    }

    // 是否有子级
    @Transient
    private Boolean hasChild;

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? "" : parentId;
    }

}
