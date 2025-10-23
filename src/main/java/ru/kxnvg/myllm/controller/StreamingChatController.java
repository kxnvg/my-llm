package ru.kxnvg.myllm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.kxnvg.myllm.service.ChatService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StreamingChatController {

    private final ChatService chatService;

    @GetMapping(value = "/chat-stream/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter talkToModel(@PathVariable Long chatId, @RequestParam String userPrompt) {
        log.info("Received new streaming prompt for chat id {}: {}", chatId, userPrompt);
        return chatService.proceedInteractionWithStreaming(chatId, userPrompt);
    }
}
