package com.jabiseo.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FCMConfig {

    @Value("${firebase.key-path}")
    private String firebaseKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            ClassPathResource resource = new ClassPathResource(firebaseKeyPath);
            InputStream serviceAccount = resource.getInputStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            log.error("Firebase ServiceAccountKey FileNotFoundException {}", e.getMessage());
        } catch (IOException e) {
            log.error("FirebaseOptions IOException {}" , e.getMessage());
        }
    }
}
