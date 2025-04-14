package com.zolp.custhumb.domain.auth.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CallbackTestApi {

    @GetMapping("/oauth/google/callback")
    public ResponseEntity<ClassPathResource> forwardToIndex() throws IOException {
        ClassPathResource indexHtml = new ClassPathResource("static/index.html");
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(indexHtml);
    }
}
