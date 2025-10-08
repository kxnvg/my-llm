package ru.kxnvg.myllm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MyLlmApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MyLlmApplication.class, args);
        ChatClient chatClient = context.getBean(ChatClient.class);
        System.out.println(chatClient.prompt().user("Дай первую строчку богемской рапсодии").call().content());
    }

}
