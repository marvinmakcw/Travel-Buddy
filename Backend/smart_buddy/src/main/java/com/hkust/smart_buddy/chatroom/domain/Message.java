package com.hkust.smart_buddy.chatroom.domain;

import com.hkust.smart_buddy.common.constants.DatabaseConstants;
import com.hkust.smart_buddy.common.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = DatabaseConstants.MESSAGE_TABLE)
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractEntity {

    @NotNull
    @Size(max = 36)
    @Column(name = DatabaseConstants.MESSAGE_ID, nullable = false, length = 36)
    private String messageId;

    @NotNull
    @Size(max = 36)
    @Column(name = DatabaseConstants.USER_ID, nullable = false, length = 36)
    private String userId;

    @NotNull
    @Column(name = DatabaseConstants.CONTENT, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Size(max = 2)
    @Column(name = DatabaseConstants.SENDER, nullable = false, length = 2)
    private String sender; // 'A' for AI, 'U' for user
}
