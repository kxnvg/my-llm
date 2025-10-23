package ru.kxnvg.myllm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.kxnvg.myllm.entity.Chat;
import ru.kxnvg.myllm.entity.ChatEntry;
import ru.kxnvg.myllm.entity.enums.Role;
import ru.kxnvg.myllm.repository.ChatRepository;
import ru.kxnvg.myllm.service.llm.PostgresChatMemory;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatClient chatClient;
    private final PostgresChatMemory postgresChatMemory;

    public List<Chat> getAllChats() {
        return chatRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Chat getChat(Long chatId) {
        Chat chat = getChatOrThrow(chatId);
        log.info("Found chat with id: {}", chatId);
        return chat;
    }

    public Chat createChat(String title) {
        Chat chat = Chat.builder()
                .title(title)
                .build();
        Chat savedChat = chatRepository.save(chat);
        log.info("Created new chat with id: {}", savedChat.getId());
        return savedChat;
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
        log.info("Deleted chat with id: {}", chatId);
    }

    @Transactional
    public void proceedInteraction(Long chatId, String prompt) {
        chatClient.prompt()
                .user(prompt)
                .advisors(
                        MessageChatMemoryAdvisor.builder(postgresChatMemory)
                        .conversationId(String.valueOf(chatId))
                        .build()
                )
                .call()
                .content();
    }

    @Transactional
    public SseEmitter proceedInteractionWithStreaming(Long chatId, String prompt) {
        SseEmitter emitter = new SseEmitter(0L);
        StringBuilder answer = new StringBuilder();

        chatClient.prompt(prompt)
                .advisors(
                        MessageChatMemoryAdvisor.builder(postgresChatMemory)
                                .conversationId(String.valueOf(chatId))
                                .build()
                )
                .stream()
                .chatResponse()
                .subscribe(
                        r -> processToken(r, emitter, answer),
                        emitter::completeWithError
                );

        return emitter;
    }

    private Chat getChatOrThrow(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> {
                    log.error("Chat with id {} not found", chatId);
                    return new EntityNotFoundException("Chat not found chat with id: " + chatId);
                });
    }

    private void processToken(ChatResponse chatResponse, SseEmitter emitter, StringBuilder answer) {
        try {
            AssistantMessage token = chatResponse.getResult().getOutput();
            emitter.send(token);
            answer.append(token.getText());
        } catch (IOException e) {
            log.error("Error sending token to client: {}", e.getMessage());
        }
    }
}
