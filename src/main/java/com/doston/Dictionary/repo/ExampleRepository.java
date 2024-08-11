package com.doston.Dictionary.repo;

import com.doston.Dictionary.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ExampleRepository.java
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {}
