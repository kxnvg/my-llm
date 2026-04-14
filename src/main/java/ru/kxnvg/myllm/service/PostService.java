package ru.kxnvg.myllm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.moderation.ModerationModel;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.model.dto.ModerationResult;
import ru.kxnvg.myllm.model.dto.PostDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final ModerationService moderationService;
    private final ModerationModel moderationModel;

    public ModerationResult createPost(PostDto postDto) {
        ModerationResult result = moderationService.moderatePost(postDto);

        switch (result.status()) {
            case OK -> log.info("Post is OK: {}", postDto.title());
            case SPAM -> log.warn("Post is SPAM: {}", postDto.title());
            case FRAUD -> log.error("Post is FRAUD: {}", postDto.title());
            case TOXIC -> log.error("Post is TOXIC: {}", postDto.title());
            case UNKNOWN -> log.error("Post moderation status is UNKNOWN for post: {}", postDto.title());
        }

        return result;
    }
}
