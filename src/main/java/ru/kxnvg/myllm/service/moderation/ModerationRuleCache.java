package ru.kxnvg.myllm.service.moderation;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kxnvg.myllm.model.entity.ModerationRule;
import ru.kxnvg.myllm.repository.ModerationRuleRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ModerationRuleCache {

    private final ModerationRuleRepository moderationRuleRepository;

    private List<ModerationRule> cachedRules = new ArrayList<>();

    @PostConstruct
    public void init() {
        reload();
    }

    @Scheduled(fixedDelay = 60000)
    public void reload() {
        this.cachedRules = moderationRuleRepository.findByIsActiveTrue();
    }

    public List<ModerationRule> getRules() {
        return cachedRules;
    }
}
