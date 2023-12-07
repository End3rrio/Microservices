package org.example.client.config;

import org.example.store.EnableStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        EnableStore.class
})
@Configuration
public class ImportConfig {
}
