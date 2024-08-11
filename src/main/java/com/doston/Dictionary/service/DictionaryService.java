
package com.doston.Dictionary.service;

import com.doston.Dictionary.dto.ExampleDTO;
import com.doston.Dictionary.dto.MeaningDTO;
import com.doston.Dictionary.dto.WordDTO;
import com.doston.Dictionary.entity.Example;
import com.doston.Dictionary.entity.Meaning;
import com.doston.Dictionary.entity.Word;
import com.doston.Dictionary.repo.WordRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private RestTemplate restTemplate;

//    @Value("${deepl.api.key}")
//    private String deeplApiKey;
//
    @Value("${dictionary.api.url}")
    private String dictionaryApiUrl;
//
//    @Value("${openai.api.key}")
//    private String openAiApiKey;

    @Value("${libretranslate.api.url}")
    private String libreTranslateUrl;

//    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public WordDTO getWordDetails(String englishWord) {
        Optional<Word> existingWord = wordRepository.findByEnglishWord(englishWord);
        if (existingWord.isPresent()) {
            return convertToDTO(existingWord.get());
        }

        JSONObject dictionaryData = fetchDictionaryData(englishWord);

        Word newWord = new Word();
        newWord.setEnglishWord(englishWord);
        newWord.setUzbekWord(translateToUzbek(englishWord));
        newWord.setCreatedAt(LocalDateTime.now());
        newWord.setUpdatedAt(LocalDateTime.now());

        if (dictionaryData != null) {
            newWord.setPronunciation(dictionaryData.optString("phonetic", null));

            JSONArray phoneticsArray = dictionaryData.optJSONArray("phonetics");
            if (phoneticsArray != null) {
                for (int i = 0; i < phoneticsArray.length(); i++) {
                    JSONObject phonetic = phoneticsArray.getJSONObject(i);
                    if (phonetic.has("audio") && !phonetic.getString("audio").isEmpty()) {
                        newWord.setPronunciationUrl(phonetic.getString("audio"));
                        break;
                    }
                }
            }

            List<String> synonyms = new ArrayList<>();
            List<String> antonyms = new ArrayList<>();
            JSONArray meaningsArray = dictionaryData.getJSONArray("meanings");

            for (int i = 0; i < meaningsArray.length(); i++) {
                JSONObject meaningObj = meaningsArray.getJSONObject(i);
                Meaning meaning = new Meaning();
                meaning.setWord(newWord);
                meaning.setPartOfSpeech(meaningObj.getString("partOfSpeech"));

                JSONArray definitionsArray = meaningObj.getJSONArray("definitions");
                for (int j = 0; j < definitionsArray.length(); j++) {
                    JSONObject definitionObj = definitionsArray.getJSONObject(j);
                    String definition = definitionObj.getString("definition");
                    meaning.setDefinition(definition);
                    meaning.setUzbekTranslation(translateToUzbek(definition));

                    String exampleSentence = definitionObj.optString("example", null);
                    if (exampleSentence != null && !exampleSentence.isEmpty()) {
                        Example example = new Example();
                        example.setExampleSentence(exampleSentence);
                        example.setMeaning(meaning);
                        meaning.getExamples().add(example);
                    }

                    synonyms.addAll(jsonArrayToList(definitionObj.optJSONArray("synonyms")));
                    antonyms.addAll(jsonArrayToList(definitionObj.optJSONArray("antonyms")));
                }

                newWord.getMeanings().add(meaning);
            }

            newWord.setSynonyms(synonyms);
            newWord.setAntonyms(antonyms);
        } else {
            Meaning meaning = new Meaning();
            meaning.setWord(newWord);
            meaning.setPartOfSpeech("unknown");
            meaning.setDefinition(englishWord);
            meaning.setUzbekTranslation(translateToUzbek(englishWord));
            newWord.getMeanings().add(meaning);
        }

        wordRepository.save(newWord);
        return convertToDTO(newWord);
    }

    private String translateToUzbek(String englishText) {
        String url = "https://libretranslate.de/translate";

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("q", englishText);
            requestBody.put("source", "en");
            requestBody.put("target", "uz");
            requestBody.put("format", "text");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            logger.info("LibreTranslate API response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                if (jsonResponse.has("translatedText")) {
                    return jsonResponse.getString("translatedText");
                } else {
                    logger.error("Unexpected response format: {}", jsonResponse);
                    return "Unexpected response format";
                }
            } else {
                logger.error("LibreTranslate API error: {} - {}", response.getStatusCode(), response.getBody());
                return "API error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            logger.error("Error translating text with LibreTranslate", e);
            return "Translation error: " + e.getMessage();
        }
    }

    private JSONObject fetchDictionaryData(String word) {
        String url = dictionaryApiUrl + word;
        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.info("Dictionary API response for word '{}': {}", word, response);
            JSONArray jsonArray = new JSONArray(response);
            if (!jsonArray.isEmpty()) {
                return jsonArray.getJSONObject(0);
            }
        } catch (Exception e) {
            logger.error("Error fetching dictionary data for word '{}': {}", word, e.getMessage());
        }
        return null;
    }





    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        }
        return list;
    }

    private WordDTO convertToDTO(Word word) {
        WordDTO wordDTO = new WordDTO();
        wordDTO.setId(word.getId());
        wordDTO.setEnglishWord(word.getEnglishWord());
        wordDTO.setUzbekWord(word.getUzbekWord());
        wordDTO.setPronunciation(word.getPronunciation());
        wordDTO.setPronunciationUrl(word.getPronunciationUrl());
        wordDTO.setSynonyms(word.getSynonyms());
        wordDTO.setAntonyms(word.getAntonyms());


        List<MeaningDTO> meaningsDTO = new ArrayList<>();
        for (Meaning meaning : word.getMeanings()) {
            MeaningDTO meaningDTO = new MeaningDTO();
            meaningDTO.setPartOfSpeech(meaning.getPartOfSpeech());
            meaningDTO.setDefinition(meaning.getDefinition());
            meaningDTO.setUzbekTranslation(meaning.getUzbekTranslation());

            List<ExampleDTO> examplesDTO = new ArrayList<>();
            for (Example example : meaning.getExamples()) {
                ExampleDTO exampleDTO = new ExampleDTO();
                exampleDTO.setExampleSentence(example.getExampleSentence());
                examplesDTO.add(exampleDTO);
            }
            meaningDTO.setExamples(examplesDTO);
            meaningsDTO.add(meaningDTO);
        }
        wordDTO.setMeanings(meaningsDTO);

        // Set any additional properties if needed
        logger.info("Returning WordDTO: {}", wordDTO);
        return wordDTO;
    }
}




//
//private String translateToUzbek(String englishText) {
//    String translatedText = translateWithMyMemory(englishText);
//    if (translatedText.equals("Translation failed")) {
//        translatedText = translateWithLibreTranslate(englishText);
//    }
//    return translatedText.equals("Translation failed") ? englishText : translatedText;
//}
//
//private String translateWithMyMemory(String englishText) {
//    String url = "https://api.mymemory.translated.net/get?q=" + URLEncoder.encode(englishText, StandardCharsets.UTF_8) + "&langpair=en|uz";
//
//    try {
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        logger.info("MyMemory API response: {}", response.getBody());
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JSONObject jsonResponse = new JSONObject(response.getBody());
//            JSONObject responseData = jsonResponse.getJSONObject("responseData");
//            return responseData.getString("translatedText");
//        } else {
//            logger.error("MyMemory API error: {}", response.getStatusCode());
//        }
//    } catch (Exception e) {
//        logger.error("Error translating text with MyMemory", e);
//    }
//    return "Translation failed";
//}
//
//private String translateWithLibreTranslate(String englishText) {
//    String url = "https://libretranslate.de/translate";
//
//    try {
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("q", englishText);
//        requestBody.put("source", "en");
//        requestBody.put("target", "uz");
//        requestBody.put("format", "text");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//        logger.info("LibreTranslate API response: {}", response.getBody());
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JSONObject jsonResponse = new JSONObject(response.getBody());
//            return jsonResponse.getString("translatedText");
//        } else {
//            logger.error("LibreTranslate API error: {}", response.getStatusCode());
//        }
//    } catch (Exception e) {
//        logger.error("Error translating text with LibreTranslate", e);
//    }
//    return "Translation failed";
//}
















//package com.doston.Dictionary.service;
//
//import com.doston.Dictionary.entity.Example;
//import com.doston.Dictionary.entity.Meaning;
//import com.doston.Dictionary.entity.Word;
//import com.doston.Dictionary.repo.WordRepository;
//import org.apache.commons.text.StringEscapeUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class DictionaryService {
//
//    private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
//
//    @Autowired
//    private WordRepository wordRepository;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${dictionary.api.url}")
//    private String dictionaryApiUrl;
//
////    private static final String MYMEMORY_API_URL = "https://api.mymemory.translated.net/get";
//
//
//    public Word addWord(String englishWord) {
//        Optional<Word> existingWord = wordRepository.findByEnglishWord(englishWord);
//        if (existingWord.isPresent()) {
//            return existingWord.get();
//        }
//
//        JSONObject dictionaryData = fetchDictionaryData(englishWord);
//
//        Word newWord = new Word();
//        newWord.setEnglishWord(englishWord);
//        newWord.setUzbekWord(translateToUzbek(englishWord));
//        newWord.setCreatedAt(LocalDateTime.now());
//        newWord.setUpdatedAt(LocalDateTime.now());
//
//        if (dictionaryData != null) {
//            newWord.setPronunciation(dictionaryData.optString("phonetic", null));
//
//            JSONArray phoneticsArray = dictionaryData.optJSONArray("phonetics");
//            if (phoneticsArray != null) {
//                for (int i = 0; i < phoneticsArray.length(); i++) {
//                    JSONObject phonetic = phoneticsArray.getJSONObject(i);
//                    if (phonetic.has("audio") && !phonetic.getString("audio").isEmpty()) {
//                        newWord.setPronunciationUrl(phonetic.getString("audio"));
//                        break;
//                    }
//                }
//            }
//
//            List<String> synonyms = new ArrayList<>();
//            List<String> antonyms = new ArrayList<>();
//            JSONArray meaningsArray = dictionaryData.getJSONArray("meanings");
//
//            for (int i = 0; i < meaningsArray.length(); i++) {
//                JSONObject meaningObj = meaningsArray.getJSONObject(i);
//                Meaning meaning = new Meaning();
//                meaning.setWord(newWord);
//                meaning.setPartOfSpeech(meaningObj.getString("partOfSpeech"));
//
//                JSONArray definitionsArray = meaningObj.getJSONArray("definitions");
//                for (int j = 0; j < definitionsArray.length(); j++) {
//                    JSONObject definitionObj = definitionsArray.getJSONObject(j);
//                    String definition = definitionObj.getString("definition");
//                    meaning.setDefinition(definition);
//                    meaning.setUzbekTranslation(translateToUzbek(definition));
//
//                    String exampleSentence = definitionObj.optString("example", null);
//                    if (exampleSentence != null && !exampleSentence.isEmpty()) {
//                        Example example = new Example();
//                        example.setExampleSentence(exampleSentence);
////                        example.setTranslation(translateToUzbek(exampleSentence));
//                        example.setMeaning(meaning);
//                        meaning.getExamples().add(example);
//                    }
//
//                    synonyms.addAll(jsonArrayToList(definitionObj.optJSONArray("synonyms")));
//                    antonyms.addAll(jsonArrayToList(definitionObj.optJSONArray("antonyms")));
//                }
//
//                newWord.getMeanings().add(meaning);
//            }
//
//            newWord.setSynonyms(synonyms);
//            newWord.setAntonyms(antonyms);
//        } else {
//            Meaning meaning = new Meaning();
//            meaning.setWord(newWord);
//            meaning.setPartOfSpeech("unknown");
//            meaning.setDefinition(englishWord);
//            meaning.setUzbekTranslation(translateToUzbek(englishWord));
//            newWord.getMeanings().add(meaning);
//        }
//
//        return wordRepository.save(newWord);
//    }
//
//    private JSONObject fetchDictionaryData(String word) {
//        String url = dictionaryApiUrl + word;
//        try {
//            String response = restTemplate.getForObject(url, String.class);
//            JSONArray jsonArray = new JSONArray(response);
//            if (jsonArray.length() > 0) {
//                return jsonArray.getJSONObject(0);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // free trial has ended at 17:02 10.08
////    private String translateToUzbek(String englishText) {
////        URI uri = UriComponentsBuilder.fromHttpUrl(MYMEMORY_API_URL)
////                .queryParam("q", englishText)
////                .queryParam("langpair", "en|uz")
////                .build()
////                .toUri();
////
////        String response = restTemplate.getForObject(uri, String.class);
////
////        JSONObject jsonResponse = new JSONObject(response);
////        JSONObject responseData = jsonResponse.getJSONObject("responseData");
////        String translatedText = responseData.getString("translatedText");
////
////        // Decode HTML entities
////        return StringEscapeUtils.unescapeHtml4(translatedText);
////    }
//
//    private String translateToUzbek(String englishText) {
//        String url = "https://api.mymemory.translated.net/get?q=" + URLEncoder.encode(englishText, StandardCharsets.UTF_8) + "&langpair=en|uz";
//
//        try {
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            logger.info("MyMemory API response: {}", response.getBody());
//            if (response.getStatusCode() == HttpStatus.OK) {
//                JSONObject jsonResponse = new JSONObject(response.getBody());
//                JSONObject responseData = jsonResponse.getJSONObject("responseData");
//                return responseData.getString("translatedText");
//            } else {
//                logger.error("MyMemory API error: {}", response.getStatusCode());
//            }
//        } catch (Exception e) {
//            logger.error("Error translating text", e);
//        }
//        return "Translation failed";
//    }
//
//
////
//    private List<String> jsonArrayToList(JSONArray jsonArray) {
//        List<String> list = new ArrayList<>();
//        if (jsonArray != null) {
//            for (int i = 0; i < jsonArray.length(); i++) {
//                list.add(jsonArray.getString(i));
//            }
//        }
//        return list;
//    }
//}