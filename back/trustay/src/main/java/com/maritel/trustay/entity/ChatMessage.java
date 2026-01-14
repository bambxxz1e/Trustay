package com.maritel.trustay.entity;

import com.maritel.trustay.constant.MessageType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_CHAT_MESSAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(columnDefinition = "TEXT")
    private String message; // 메시지 내용

    @Enumerated(EnumType.STRING)
    private MessageType messageType; // TEXT, IMAGE, CONTRACT(계약서)

    private boolean isRead; // 읽음 확인

    private LocalDateTime sentAt; // 전송 시간 (BaseEntity 안쓰고 별도 관리 추천)

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, String message, MessageType messageType) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
        this.isRead = false;
        this.sentAt = LocalDateTime.now();
    }
}