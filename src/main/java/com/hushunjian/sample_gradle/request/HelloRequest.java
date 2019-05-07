package com.hushunjian.sample_gradle.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HelloRequest {

    @ApiModelProperty(value = "测试message")
    private String message;
}
