package com.rms.demo.client.multipart.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Service
@AllArgsConstructor
public class MultipartUploadClient {

    private final WebClient client;


    public Mono<String> sendMultipartWithoutLength() throws URISyntaxException {
        return client.post()
                .uri("http://localhost:8080/without/multipart")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(files())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> sendMultipartWithLength() throws URISyntaxException {
        var length = length();

        return client.post()
                .uri("http://localhost:8080/with/multipart")
                .contentLength(length)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(files())
                .retrieve()
                .bodyToMono(String.class);
    }

    private MultiValueMap<String, HttpEntity<?>> files() throws URISyntaxException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file1", new FileSystemResource(getFileFromResource("1.txt")));
        builder.part("file2", new FileSystemResource(getFileFromResource("2.txt")));
        builder.part("file3", new FileSystemResource(getFileFromResource("3.txt")));


        return builder.build();
    }

    private long length() throws URISyntaxException {
        long length = getFileFromResource("1.txt").length();
        length += getFileFromResource("2.txt").length();
        length += getFileFromResource("3.txt").length();

        log.info("Content length {}", length);

        return length;
    }

    private File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);

        return new File(resource.toURI());
    }

}
