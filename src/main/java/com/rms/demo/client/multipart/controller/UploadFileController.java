package com.rms.demo.client.multipart.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UploadFileController {


    @PostMapping(value = "/with/multipart",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<String> multipart(@RequestHeader(HttpHeaders.CONTENT_LENGTH) Long contentLength,
                                  @RequestPart("file1") FilePart file1,
                                  @RequestPart("file2") FilePart file2,
                                  @RequestPart("file3") FilePart file3) {
        log.info("Uploaded content with length {}", contentLength);
        return Mono.zip(md5(file1), md5(file2), md5(file3))
                .map(md5Res -> "Uploaded: " + md5Res.getT1() + ", " + md5Res.getT2() + ", " + md5Res.getT3());
    }

    @PostMapping(value = "/without/multipart",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<String> multipartWithout(
            @RequestPart("file1") FilePart file1,
            @RequestPart("file2") FilePart file2,
            @RequestPart("file3") FilePart file3) {
        log.info("Uploaded content without length");
        return Mono.zip(md5(file1), md5(file2), md5(file3))
                .map(md5Res -> "Uploaded: " + md5Res.getT1() + ", " + md5Res.getT2() + ", " + md5Res.getT3());
    }

    private Mono<String> md5(FilePart filePart) {
        return filePart.content().map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return bytes;
        }).collectList().flatMap(bytes -> {
            int count = 0;
            for (byte[] aByte : bytes) {
                count += aByte.length;
            }

            int c = 0;
            byte[] allDate = new byte[count];
            for (byte[] aByte : bytes) {
                for (byte b : aByte) {
                    allDate[c] = b;
                    c += 1;
                }
            }

            String result = filePart.filename() +  ": " + DigestUtils.md5Hex(allDate);

            return Mono.just(result);
        });
    }

}
