package com.doston.Dictionary.repo;

import com.doston.Dictionary.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// WordRepository.java
@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByEnglishWord(String englishWord);
}

