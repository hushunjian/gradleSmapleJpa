package com.hushunjian.sample_gradle.service;

import com.hushunjian.sample_gradle.entity.HelloEntity;
import com.hushunjian.sample_gradle.repo.HelloRepo;
import com.hushunjian.sample_gradle.request.HelloRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.hushunjian.sample_gradle.copier.HelloCopier.HELLO_COPIER;

@Service
@Transactional
public class HelloService {

    @Autowired
    private HelloRepo helloRepo;

    public HelloEntity save(HelloRequest helloRequest) {
        HelloEntity helloEntity = HELLO_COPIER.toHelloEntity(helloRequest);
        helloRepo.save(helloEntity);
        return helloEntity;
    }
}
