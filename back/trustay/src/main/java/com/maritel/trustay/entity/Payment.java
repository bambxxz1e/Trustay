package com.maritel.trustay.entity;

import com.maritel.trustay.constant.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 돈을 낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String targetAccount; // 받는 계좌

    @Enumerated(EnumType.STRING)
    private PaymentType type; // RENT(월세), UTILITY(공과금), DUTCH(더치페이)

    private LocalDateTime transactionDate;

    private boolean isAutoTransfer; // 자동이체 여부

    @Builder
    public Payment(Member member, Long amount, String targetAccount, PaymentType type, Contract contract) {
        this.member = member;
        this.amount = amount;
        this.targetAccount = targetAccount;
        this.type = type;
        this.contract = contract;
        this.transactionDate = LocalDateTime.now();
    }
}