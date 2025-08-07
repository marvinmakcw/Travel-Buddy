package com.hkust.smart_buddy.chatroom.service;

import com.hkust.smart_buddy.chatroom.domain.Message;
import com.hkust.smart_buddy.chatroom.dto.MessageRequestDto;
import com.hkust.smart_buddy.chatroom.dto.MessageResponseDto;
import com.hkust.smart_buddy.chatroom.repository.MessageRepository;
import com.hkust.smart_buddy.common.constants.DatabaseConstants;
import com.hkust.smart_buddy.common.constants.JwtConstants;
import com.hkust.smart_buddy.chatroom.constants.MessageConstants;
import com.hkust.smart_buddy.common.util.JwtUtil;
import com.hkust.smart_buddy.common.util.UuidUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final JwtUtil jwtUtil;
    private final MessageRepository messageRepository;

    public Page<MessageResponseDto> getMessages(String token, Pageable pageable) {
        String userId = getCurrentUserId(token);
        Page<Message> messages = messageRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
        List<MessageResponseDto> messageResponseList = messages.getContent().stream()
                .map(this::convertToResponseDto)
                .toList();
        return new PageImpl<>(messageResponseList, pageable, messages.getTotalElements());
    }

    public MessageResponseDto createMessages(MessageRequestDto requestDto) {
        String userId = getCurrentUserId(requestDto.getToken());
        saveUserMessage(userId, requestDto);
        String aiAdvice = getAiAdviceMessage(requestDto.getContent());
        return saveAndReturnAiMessage(userId, aiAdvice);
    }

    private void saveUserMessage(String userId, MessageRequestDto requestDto) {
        Message message = Message.builder()
                .messageId(generateMessageId())
                .userId(userId)
                .content(requestDto.getContent())
                .sender(MessageConstants.SENDER_USER)
                .createdBy(MessageConstants.SENDER_USER)
                .createdDate(LocalDateTime.now())
                .lastModifiedBy(MessageConstants.SENDER_USER)
                .lastModifiedDate(LocalDateTime.now())
                .recordVersion(DatabaseConstants.DEFAULT_RECORD_VERSION)
                .build();

        messageRepository.saveAndFlush(message);
    }

    private MessageResponseDto saveAndReturnAiMessage(String userId, String content) {
        Message message = Message.builder()
                .messageId(generateMessageId())
                .userId(userId)
                .content(content)
                .sender(MessageConstants.SENDER_AI)
                .createdBy(MessageConstants.SENDER_AI)
                .createdDate(LocalDateTime.now())
                .lastModifiedBy(MessageConstants.SENDER_AI)
                .lastModifiedDate(LocalDateTime.now())
                .recordVersion(DatabaseConstants.DEFAULT_RECORD_VERSION)
                .build();

        messageRepository.saveAndFlush(message);
        return convertToResponseDto(message);
    }

    private MessageResponseDto convertToResponseDto(Message message) {
        return MessageResponseDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .createdDateTime(message.getCreatedDate())
                .build();
    }

    private String getAiAdviceMessage(String query) {
        //TODO: Implement AI advice retrieval logic
        return MessageConstants.AI_ADVICE_PREFIX + query;
    }

    private String generateMessageId() {
        return UuidUtil.generateUniqueId(candidateId ->
            messageRepository.findByMessageId(candidateId).isPresent()
        );
    }

    private String getCurrentUserId(String token) {
        Claims claims = jwtUtil.decodeToken(token);
        return (String) claims.get(JwtConstants.USER_ID_CLAIM);
    }
}
