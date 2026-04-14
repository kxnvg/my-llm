package ru.kxnvg.myllm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.model.dto.ModerationResult;
import ru.kxnvg.myllm.model.dto.PostDto;
import ru.kxnvg.myllm.model.enums.ModerationStatus;

@Slf4j
@Service
public class ModerationService {

    private final ChatClient moderationChatClient;
    private final ObjectMapper objectMapper;

    public ModerationService(@Qualifier("moderationChatClient") ChatClient moderationChatClient, ObjectMapper objectMapper) {
        this.moderationChatClient = moderationChatClient;
        this.objectMapper = objectMapper;
    }

    public ModerationResult moderatePost(PostDto postDto) {
        String prompt = getPrompt(postDto.title(), postDto.content());

        String response = moderationChatClient.prompt()
                .user(prompt)
                .call()
                .content();
        log.info("Moderation response for post '{}': {}", postDto.title(), response);

        return parse(response);
    }

    private String getPrompt(String title, String text) {
        return """
                Ты система модерации объявлений. У объявлений есть заголовок и текст.
                
                Классифицируй текст в одну категорию:
                - OK
                - SPAM
                - FRAUD
                - TOXIC
                
                Верни ответ в формате JSON:
                {
                  "status": "...",
                  "reason": "..."
                }
                Отвечай ТОЛЬКО JSON. Без текста. Без пояснений.
                
                Заголовок объявления: %s
                Текст объявления:
                %s
                """.formatted(title, text);
    }

    private ModerationResult parse(String json) {
        try {
            json = json
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();
            return objectMapper.readValue(json, ModerationResult.class);
        } catch (Exception e) {
            log.error("Failed to parse moderation response: {}", json, e);
            return new ModerationResult(ModerationStatus.UNKNOWN, "parse error");
        }
    }
}
