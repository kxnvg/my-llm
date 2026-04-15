package ru.kxnvg.myllm.model.dto;

import java.util.List;

public record RuleCheckResult(
        int score,
        List<String> triggers
) {
    public boolean isHardViolation() {
        return score >= 5;
    }

    public boolean isSuspicious() {
        return score >= 2;
    }
}