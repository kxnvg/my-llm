package ru.kxnvg.myllm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.ai.chat.messages.Message;
import ru.kxnvg.myllm.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_entry")
public class ChatEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static ChatEntry fromMessage(Message message) {
        return ChatEntry.builder()
                .content(message.getText())
                .role(Role.getRole(message.getMessageType().getValue()))
                .build();
    }

    public Message toMessage() {
        return role.getMessage(content);
    }

    @Override
    public String toString() {
        return "ChatEntry{" +
               "id=" + id +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatEntry chatEntry = (ChatEntry) o;
        return Objects.equals(id, chatEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
