package ru.kxnvg.myllm.repository;

import org.springframework.stereotype.Component;
import ru.kxnvg.myllm.model.entity.ModerationRule;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModerationRuleRepository {

    public List<ModerationRule> findByIsActiveTrue() {
        return new ArrayList<>();
    }
}
