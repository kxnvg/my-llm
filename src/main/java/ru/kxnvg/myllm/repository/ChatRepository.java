package ru.kxnvg.myllm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kxnvg.myllm.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
