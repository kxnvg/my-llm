package ru.kxnvg.myllm.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("user") {
        @Override
        public Message getMessage(String prompt) {
            return new UserMessage(prompt);
        }
    },

    ASSISTANT("assistant") {
        @Override
        public Message getMessage(String prompt) {
            return new AssistantMessage(prompt);
        }
    },

    SYSTEM("system") {
        @Override
        public Message getMessage(String prompt) {
            return new SystemMessage(prompt);
        }
    };

    private final String value;

    public static Role getRole(String value) {
        for (Role role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }

    public abstract Message getMessage(String prompt);
}
