package ru.kxnvg.myllm.service.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.model.dto.ModerationResult;
import ru.kxnvg.myllm.model.dto.PostDto;
import ru.kxnvg.myllm.model.dto.RuleCheckResult;
import ru.kxnvg.myllm.model.enums.ModerationStatus;

@RequiredArgsConstructor
@Service
public class ModerationService {

    private final LlmModerationService llmModerationService;
    private final LocalModerationService localModerationService;

    public ModerationResult moderatePost(PostDto postDto) {
        RuleCheckResult checkResult = localModerationService.localModerate(postDto);

        if (checkResult.isHardViolation()) {
            return new ModerationResult(
                    ModerationStatus.SPAM,
                    "Reasons: " + checkResult.triggers()
            );
        }

        if (checkResult.isSuspicious()) {
            return llmModerationService.moderatePost(postDto);
        }

        return new ModerationResult(
                ModerationStatus.OK,
                "OK"
        );
    }
}
