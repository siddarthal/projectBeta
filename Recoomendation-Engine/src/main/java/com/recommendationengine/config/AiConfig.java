package com.recommendationengine.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.autoconfigure.vertexai.gemini.VertexAiGeminiAutoConfiguration;

@Configuration
@EnableAutoConfiguration(exclude = {VertexAiGeminiAutoConfiguration.class})
public class AiConfig {
    // This configuration disables the Gemini AI integration for now
} 