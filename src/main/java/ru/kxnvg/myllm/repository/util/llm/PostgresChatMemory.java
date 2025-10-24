package ru.kxnvg.myllm.repository.util.llm;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import ru.kxnvg.myllm.entity.Chat;
import ru.kxnvg.myllm.entity.ChatEntry;
import ru.kxnvg.myllm.repository.ChatRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Builder
public class PostgresChatMemory implements ChatMemory {

    private ChatRepository chatMemoryRepository;
    private int maxMessages;

    @Override
    public void add(String conversationId, List<Message> messages) {
        Chat chat = getChatOrThrow(conversationId);
        for (Message message : messages) {
            chat.addEntry(ChatEntry.fromMessage(message));
        }
        chatMemoryRepository.save(chat);
    }

    @Override
    public List<Message> get(String conversationId) {
        Chat chat = getChatOrThrow(conversationId);
        return chat.getHistory().stream()
                .sorted(Comparator.comparing(ChatEntry::getCreatedAt).reversed())
                .limit(maxMessages)
                .map(ChatEntry::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
//        not implemented
    }

    private Chat getChatOrThrow(String chatId) {
        return chatMemoryRepository.findById(Long.valueOf(chatId))
                .orElseThrow(() -> {
                    log.error("Chat with id {} not found", chatId);
                    return new EntityNotFoundException("Chat not found");
                });
    }
}
