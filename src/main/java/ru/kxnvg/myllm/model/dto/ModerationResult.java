package ru.kxnvg.myllm.model.dto;

import ru.kxnvg.myllm.model.enums.ModerationStatus;

public record ModerationResult(
        ModerationStatus status,
        String reason
) {}
