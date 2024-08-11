package com.doston.Dictionary.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "meanings")
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    @JsonBackReference
    private Word word;

    private String partOfSpeech;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(columnDefinition = "TEXT")
    private String uzbekTranslation;

    @OneToMany(mappedBy = "meaning", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Example> examples = new ArrayList<>();


    // ... getters and setters ...
}