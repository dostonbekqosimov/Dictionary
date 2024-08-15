



package com.doston.Dictionary.controller;

import com.doston.Dictionary.dto.WordDTO;
import com.doston.Dictionary.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/dictionary")
public class DictionaryController {
    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/search")
    public String searchWord(@RequestParam(name = "query", required = false, defaultValue = "") String query, Model model) {

        query = query.replaceAll("^\"|\"$", "");
        WordDTO wordDTO = dictionaryService.getWordDetails(query);
        model.addAttribute("word", wordDTO);
        return "search";
    }

    @GetMapping("")
    public String getHomePage() {
        return "home";
    }


}





















//package com.doston.Dictionary.controller;
//
//import com.doston.Dictionary.entity.Word;
//import com.doston.Dictionary.service.DictionaryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/dictionary")
//public class DictionaryController {
//
//    @Autowired
//    private DictionaryService dictionaryService;
//
//    @PostMapping("/word")
//    public ResponseEntity<Word> addWord(@RequestBody String englishWord) {
//        // Remove quotes if present
//        englishWord = englishWord.replaceAll("^\"|\"$", "");
//        Word word = dictionaryService.addWord(englishWord);
//        return ResponseEntity.status(HttpStatus.CREATED).body(word);
//    }
//
//    @GetMapping("/word/{englishWord}")
//    public ResponseEntity<Word> getWord(@PathVariable String englishWord) {
//        Word word = dictionaryService.addWord(englishWord); // This will fetch or create the word
//        return ResponseEntity.ok(word);
//    }
//}
