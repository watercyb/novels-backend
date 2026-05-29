package com.blueberryjoy.novel.config;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HtmlSanitizerConfig {

    @Bean
    public PolicyFactory policyFactory() {

        return new HtmlPolicyBuilder()

                // Elements
                .allowElements(
                        "h1", "h2", "h3", "h4", "h5", "h6",
                        "p", "div", "span", "br",
                        "strong", "b",
                        "em", "i",
                        "u", "s", "strike",
                        "ul", "ol", "li",
                        "table", "thead", "tbody", "tr", "td", "th",
                        "a"
                )

                // Links
                .allowAttributes("href")
                .onElements("a")
                .allowUrlProtocols("http", "https", "mailto")

                // TinyMCE inline styles
                .allowStyling()

                // Optional: preserve TinyMCE metadata
                .allowAttributes(
                        "data-start",
                        "data-end",
                        "data-section-id"
                ).globally()

                .toFactory();
    }
}