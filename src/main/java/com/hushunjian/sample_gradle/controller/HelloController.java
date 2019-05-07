package com.hushunjian.sample_gradle.controller;

import com.hushunjian.sample_gradle.service.HelloService;
import com.hushunjian.sample_gradle.entity.HelloEntity;
import com.hushunjian.sample_gradle.request.HelloRequest;
import com.hushunjian.sample_gradle.response.HelloResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import static com.hushunjian.sample_gradle.copier.HelloCopier.HELLO_COPIER;

@RequestMapping("hello")
@RestController(value = "hello")
@EnableAutoConfiguration
public class HelloController {

    @Autowired
    private HelloService helloService;

    @ResponseBody
    @GetMapping("sayHello")
    @ApiOperation(value = "sayHello")
    public String sayHello(){
        return "hello_world!";
    }


    @ResponseBody
    @PostMapping("helloMapper")
    @ApiOperation(value = "helloMapper")
    public HelloResponse helloMapper(@RequestBody HelloRequest helloRequest){
        return HELLO_COPIER.toHelloResponse(helloRequest);
    }

    @ResponseBody
    @PostMapping("helloJpa")
    @ApiOperation(value = "helloJpa")
    public HelloResponse helloJpa(@RequestBody HelloRequest helloRequest){
        HelloEntity hello = helloService.save(helloRequest);
        return HELLO_COPIER.toHelloResponse(hello);
    }

}
