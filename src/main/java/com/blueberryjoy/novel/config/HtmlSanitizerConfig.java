package com.blueberryjoy.novel.config;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HtmlSanitizerConfig {

    @Bean
    public PolicyFactory htmlPolicy() {
        return Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.BLOCKS);
    }
}