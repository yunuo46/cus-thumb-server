package com.zolp.custhumb.infra.domain.gcs;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class GcsSignedUrlService {

    private final Storage storage;

    public GcsSignedUrlService(@Value("${gcp.credentials.path}") Resource keyFile) throws IOException {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyFile.getInputStream()))
                .build()
                .getService();
    }

    public URL generateUploadUrl(String bucketName, String objectName, String contentType, int expireMinutes) {
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                .setContentType(contentType)
                .build();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);

        return storage.signUrl(
                blobInfo,
                expireMinutes,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(headers),
                Storage.SignUrlOption.withV4Signature()
        );
    }
}
