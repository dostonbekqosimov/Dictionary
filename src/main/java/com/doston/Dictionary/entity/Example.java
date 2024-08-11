package com.doston.Dictionary.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name = "examples")
public class Example {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meaning_id", nullable = false )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Meaning meaning;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

//    @Column(name = "uzbek_translation", columnDefinition = "TEXT")
//    private String translation;

    // Getters and setters
}

