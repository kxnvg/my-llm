package ru.kxnvg.myllm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kxnvg.myllm.entity.Chat;
import ru.kxnvg.myllm.service.ChatService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/")
    public String mainPage(ModelMap modelMap) {
        log.info("Accessed main page");

        modelMap.addAttribute("chats", chatService.getAllChats());
        return "chat";
    }

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable Long chatId, ModelMap modelMap) {
        log.info("Accessed chat page with id: {}", chatId);

        modelMap.addAttribute("chats", chatService.getAllChats());
        modelMap.addAttribute("chat", chatService.getChat(chatId));
        return "chat";
    }

    @PostMapping("/chat/new")
    public String createChat(@RequestParam String title) {
        log.info("Creating new chat with title: {}", title);

        Chat chat = chatService.createChat(title);
        return "redirect:/chat/" + chat.getId();
    }


    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable Long chatId) {
        log.info("Deleting chat with id: {}", chatId);

        chatService.deleteChat(chatId);
        return "redirect:/";
    }
}
