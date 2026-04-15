package ru.kxnvg.myllm.model.entity;

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
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kxnvg.myllm.model.enums.RuleType;
import ru.kxnvg.myllm.model.enums.Severity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Entity
//@Table(name = "moderation_rules")
public class ModerationRule {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "type")
    private RuleType type;

//    @Column(name = "value")
    private String value;

//    @Enumerated(EnumType.STRING)
//    @Column
    private Severity severity;

//    @Column(name = "is_active")
    private boolean isActive;
}
