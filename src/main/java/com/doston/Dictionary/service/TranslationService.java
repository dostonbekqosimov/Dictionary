//package com.doston.Dictionary.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
////import okhttp3.*;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//public class TranslationService {
//
//    private static final String OPENAI_API_KEY = "your-openai-api-key";
//    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
//
//    public String translateWithGPT(String text, String sourceLang, String targetLang) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        String prompt = String.format("Translate the following text from %s to %s:\n\n%s", sourceLang, targetLang, text);
//
//        String requestBody = "{\n" +
//                             "  \"model\": \"text-davinci-003\",\n" +
//                             "  \"prompt\": \"" + prompt + "\",\n" +
//                             "  \"max_tokens\": 100,\n" +
//                             "  \"temperature\": 0.5\n" +
//                             "}";
//
//        Request request = new Request.Builder()
//                .url(OPENAI_API_URL)
//                .header("Authorization", "Bearer " + OPENAI_API_KEY)
//                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected code " + response);
//            }
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(response.body().string());
//            return jsonNode.get("choices").get(0).get("text").asText().trim();
//        }
//    }
//}
