package ru.kxnvg.myllm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.model.dto.ModerationResult;
import ru.kxnvg.myllm.model.dto.PostDto;
import ru.kxnvg.myllm.service.moderation.LlmModerationService;
import ru.kxnvg.myllm.service.moderation.ModerationService;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final ModerationService moderationService;
    private final LlmModerationService llmModerationService;

    public ModerationResult createPost(PostDto postDto) {
        ModerationResult result = llmModerationService.moderatePost(postDto);
        return logResult(postDto.title(), result);
    }

    public ModerationResult createPost_newVersion(PostDto postDto) {
        ModerationResult result = moderationService.moderatePost(postDto);
        return logResult(postDto.title(), result);
    }

    private ModerationResult logResult(String title, ModerationResult result) {
        switch (result.status()) {
            case OK -> log.info("Post is OK: {}", title);
            case SPAM -> log.warn("Post is SPAM: {}", title);
            case FRAUD -> log.error("Post is FRAUD: {}", title);
            case TOXIC -> log.error("Post is TOXIC: {}", title);
            case UNKNOWN -> log.error("Post moderation status is UNKNOWN for post: {}", title);
        }

        return result;
    }
}
