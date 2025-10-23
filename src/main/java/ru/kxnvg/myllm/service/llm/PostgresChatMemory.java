package ru.kxnvg.myllm.service.llm;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kxnvg.myllm.entity.Chat;
import ru.kxnvg.myllm.entity.ChatEntry;
import ru.kxnvg.myllm.repository.ChatRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostgresChatMemory implements ChatMemory {

    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            Chat chat = getChatOrThrow(conversationId);
            chat.addEntry(ChatEntry.fromMessage(message));
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        Chat chat = getChatOrThrow(conversationId);
        return chat.getHistory().stream()
                .map(ChatEntry::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
//        not implemented
    }

    private Chat getChatOrThrow(String chatId) {
        return chatRepository.findById(Long.valueOf(chatId))
                .orElseThrow(() -> {
                    log.error("Chat with id {} not found", chatId);
                    return new EntityNotFoundException("Chat not found");
                });
    }
}
