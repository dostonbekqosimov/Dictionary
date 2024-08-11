package com.doston.Dictionary.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "english_word", nullable = false)
    private String englishWord;

    @Column(name = "uzbek_word", nullable = false)
    private String uzbekWord;

    private String pronunciation;

    // Later should be updated
    private String pronunciationUrl;

    @ElementCollection
    private List<String> synonyms = new ArrayList<>();

    @ElementCollection
    private List<String> antonyms = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Meaning> meanings = new ArrayList<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Custom Getters for collections to prevent null values

    public List<String> getSynonyms() {
        return synonyms == null ? new ArrayList<>() : synonyms;
    }

    public List<String> getAntonyms() {
        return antonyms == null ? new ArrayList<>() : antonyms;
    }

    public List<Meaning> getMeanings() {
        return meanings == null ? new ArrayList<>() : meanings;
    }

    public List<Comment> getComments() {
        return comments == null ? new ArrayList<>() : comments;
    }

    // Other getters and setters (if needed) can be added here
}
