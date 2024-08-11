package com.doston.Dictionary.repo;

import com.doston.Dictionary.entity.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// MeaningRepository.java
@Repository
public interface MeaningRepository extends JpaRepository<Meaning, Long> {}
