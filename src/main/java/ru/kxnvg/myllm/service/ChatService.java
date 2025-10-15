package ru.kxnvg.myllm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.entity.Chat;
import ru.kxnvg.myllm.repository.ChatRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Chat getChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found chat with id: " + chatId));
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
}
