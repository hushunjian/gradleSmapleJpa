package com.hushunjian.sample_gradle.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HelloResponse {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "测试message")
    private String message;
}
