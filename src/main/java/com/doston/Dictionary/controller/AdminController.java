package com.doston.Dictionary.controller;

import com.doston.Dictionary.dto.WordDTO;
import com.doston.Dictionary.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DictionaryService dictionaryService;


    @GetMapping()
    public String getAdminPage() {
        return "admin";
    }

    @PostMapping("/search")
    public String searchWord(@RequestParam(name = "query", required = false, defaultValue = "") String query, Model model) {
        query = query.replaceAll("^\"|\"$", "");
        WordDTO wordDTO = dictionaryService.getWordDetails(query);
        model.addAttribute("word", wordDTO);
        return "admin";
    }
}
