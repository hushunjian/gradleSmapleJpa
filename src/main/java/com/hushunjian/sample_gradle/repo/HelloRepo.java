package com.hushunjian.sample_gradle.repo;

import com.hushunjian.sample_gradle.entity.HelloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloRepo extends JpaRepository<HelloEntity, String> {

}
