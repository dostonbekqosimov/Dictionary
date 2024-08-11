package com.doston.Dictionary.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WordDTO {

    private Long id;
    private String englishWord;
    private String uzbekWord;
    private String pronunciation;
    private String pronunciationUrl;
    private List<String> synonyms;
    private List<String> antonyms;
    private String partOfSpeech;
    private List<ExampleDTO> examples;
    private List<MeaningDTO> meanings;
    private List<CommentDTO> comments;
}
