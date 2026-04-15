package ru.kxnvg.myllm.service.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.model.dto.PostDto;
import ru.kxnvg.myllm.model.dto.RuleCheckResult;
import ru.kxnvg.myllm.model.entity.ModerationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class LocalModerationService {

    private final ModerationRuleCache cache;

    public RuleCheckResult localModerate(PostDto postDto) {
        String text = postDto.title().toLowerCase() + " " + postDto.content().toLowerCase();
        int score = 0;
        List<String> triggers = new ArrayList<>();

        for (ModerationRule rule : cache.getRules()) {

            boolean matched = switch (rule.getType()) {
                case BLACKLIST, LINK -> text.contains(rule.getValue().toLowerCase());
                case REGEX -> Pattern.compile(rule.getValue(), Pattern.CASE_INSENSITIVE)
                        .matcher(text)
                        .find();
            };

            if (matched) {
                triggers.add(rule.getValue());

                score += switch (rule.getSeverity()) {
                    case LOW -> 1;
                    case MEDIUM -> 2;
                    case HIGH -> 3;
                };
            }
        }

        return new RuleCheckResult(score, triggers);
    }
}
