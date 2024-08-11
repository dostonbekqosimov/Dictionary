package com.doston.Dictionary.dto;

import lombok.Data;

import java.util.List;

@Data
public class MeaningDTO {

    private Long id;
    private String partOfSpeech;
    private String definition;
    private String uzbekTranslation;
    private List<String> synonyms;
    private List<String> antonyms;

    private List<ExampleDTO> examples;
}
