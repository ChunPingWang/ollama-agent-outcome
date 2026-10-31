package com.github.chunpingwang.ollamaagentoutcome.bdd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.resttestclient.TestRestTemplate;

/**
 * BDD 測試環境所需的 Web Bean 配置。
 */
@Configuration
class BddWebConfig {

    @Bean
    TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
}
