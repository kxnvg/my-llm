package ru.kxnvg.myllm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kxnvg.myllm.repository.ChatRepository;
import ru.kxnvg.myllm.repository.util.llm.PostgresChatMemory;

@Configuration
@RequiredArgsConstructor
public class LlmConfig {

    private final ChatRepository chatRepository;
    private final VectorStore vectorStore;

    @Value("${app.llm.chat-memory.max-messages:12}")
    private int maxMemoryMessages;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(buildHistoryAdvisor(), buildRagAdvisor())
                .build();
    }

    private Advisor buildHistoryAdvisor() {
        return MessageChatMemoryAdvisor
                .builder(buildChatMemory())
                .build();

    }

    private Advisor buildRagAdvisor() {
        return QuestionAnswerAdvisor
                .builder(vectorStore)
                .promptTemplate(buildPromptTemplate())
                .build();
    }

    private ChatMemory buildChatMemory() {
        return PostgresChatMemory.builder()
                .maxMessages(maxMemoryMessages)
                .chatMemoryRepository(chatRepository)
                .build();
    }

    private PromptTemplate buildPromptTemplate() {
        return new PromptTemplate(
                "{query}\n\n" +
                "Контекст:\n" +
                "---------------------\n" +
                "{question_answer_context}\n" +
                "---------------------\n\n" +
                "Отвечай только на основе контекста выше. Если информации нет в контексте, сообщи, что не можешь ответить."
        );
    }
}
