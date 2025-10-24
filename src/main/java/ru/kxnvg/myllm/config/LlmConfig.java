package ru.kxnvg.myllm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kxnvg.myllm.repository.ChatRepository;
import ru.kxnvg.myllm.repository.util.llm.PostgresChatMemory;

@Configuration
@RequiredArgsConstructor
public class LlmConfig {

    private final ChatRepository chatRepository;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(buildAdvisor())
                .build();
    }

    private Advisor buildAdvisor() {
        return MessageChatMemoryAdvisor
                .builder(buildChatMemory())
                .build();

    }

    private ChatMemory buildChatMemory() {
        return PostgresChatMemory.builder()
                .maxMessages(2)
                .chatMemoryRepository(chatRepository)
                .build();
    }
}
