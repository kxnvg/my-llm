package ru.kxnvg.myllm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kxnvg.myllm.entity.Chat;
import ru.kxnvg.myllm.entity.ChatEntry;
import ru.kxnvg.myllm.entity.enums.Role;
import ru.kxnvg.myllm.repository.ChatRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatClient chatClient;

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
        addChatEntry(chatId, prompt, Role.USER);
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        addChatEntry(chatId, answer, Role.ASSISTANT);
    }

    private void addChatEntry(Long chatId, String prompt, Role role) {
        Chat chat = getChatOrThrow(chatId);
        chat.addEntry(
                ChatEntry.builder()
                        .content(prompt)
                        .role(role)
                        .chat(chat)
                        .build()
        );
    }

    private Chat getChatOrThrow(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found chat with id: " + chatId));
    }
}
