package com.hushunjian.sample_gradle.copier;

import com.hushunjian.sample_gradle.entity.HelloEntity;
import com.hushunjian.sample_gradle.request.HelloRequest;
import com.hushunjian.sample_gradle.response.HelloResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HelloCopier {

    HelloCopier HELLO_COPIER = Mappers.getMapper(HelloCopier.class);

    HelloResponse toHelloResponse(HelloRequest helloRequest);

    HelloResponse toHelloResponse(HelloEntity helloEntity);

    HelloEntity toHelloEntity(HelloRequest helloRequest);
}
