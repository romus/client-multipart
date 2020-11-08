package com.rms.demo.client.multipart.controller;

import com.rms.demo.client.multipart.client.MultipartUploadClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;

@RestController
@AllArgsConstructor
public class SendController {

    private final MultipartUploadClient client;

    @GetMapping("/with")
    public Mono<String> sendWith() throws URISyntaxException {
        return client.sendMultipartWithLength();
    }

    @GetMapping("/without")
    public Mono<String> sendWithout() throws URISyntaxException {
        return client.sendMultipartWithoutLength();
    }

}
