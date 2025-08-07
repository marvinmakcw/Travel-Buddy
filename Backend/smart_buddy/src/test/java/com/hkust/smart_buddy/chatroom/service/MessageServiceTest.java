package com.hkust.smart_buddy.chatroom.service;

import com.hkust.smart_buddy.chatroom.domain.Message;
import com.hkust.smart_buddy.chatroom.dto.MessageRequestDto;
import com.hkust.smart_buddy.chatroom.dto.MessageResponseDto;
import com.hkust.smart_buddy.chatroom.repository.MessageRepository;
import com.hkust.smart_buddy.common.constants.DatabaseConstants;
import com.hkust.smart_buddy.common.constants.JwtConstants;
import com.hkust.smart_buddy.chatroom.constants.MessageConstants;
import com.hkust.smart_buddy.common.exception.InvalidJwtTokenException;
import com.hkust.smart_buddy.common.util.JwtUtil;
import com.hkust.smart_buddy.common.util.UuidUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private String validToken;
    private String userId;
    private MessageRequestDto messageRequestDto;
    private Message userMessage;
    private Message aiMessage;
    private Claims claims;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        validToken = "valid-jwt-token";
        userId = "user-123-uuid";

        messageRequestDto = MessageRequestDto.builder()
                .content("I want to travel to Japan")
                .token(validToken)
                .build();

        userMessage = Message.builder()
                .messageId("msg-user-123")
                .userId(userId)
                .content("I want to travel to Japan")
                .sender(MessageConstants.SENDER_USER)
                .createdBy(MessageConstants.SENDER_USER)
                .createdDate(LocalDateTime.now())
                .lastModifiedBy(MessageConstants.SENDER_USER)
                .lastModifiedDate(LocalDateTime.now())
                .recordVersion(DatabaseConstants.DEFAULT_RECORD_VERSION)
                .build();

        aiMessage = Message.builder()
                .messageId("msg-ai-123")
                .userId(userId)
                .content(MessageConstants.AI_ADVICE_PREFIX + "I want to travel to Japan")
                .sender(MessageConstants.SENDER_AI)
                .createdBy(MessageConstants.SENDER_AI)
                .createdDate(LocalDateTime.now())
                .lastModifiedBy(MessageConstants.SENDER_AI)
                .lastModifiedDate(LocalDateTime.now())
                .recordVersion(DatabaseConstants.DEFAULT_RECORD_VERSION)
                .build();

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(JwtConstants.USER_ID_CLAIM, userId);
        claims = Jwts.claims(claimsMap);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getMessages_ValidToken_ReturnsMessagePageWithCorrectPagination() {
        List<Message> messageList = Arrays.asList(userMessage, aiMessage);
        Page<Message> messagePage = new PageImpl<>(messageList, pageable, 15);

        when(jwtUtil.decodeToken(validToken)).thenReturn(claims);
        when(messageRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable))
                .thenReturn(messagePage);

        Page<MessageResponseDto> result = messageService.getMessages(validToken, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        assertEquals("I want to travel to Japan", result.getContent().get(0).getContent());
        assertEquals(MessageConstants.SENDER_USER, result.getContent().get(0).getSender());
        assertEquals(MessageConstants.AI_ADVICE_PREFIX + "I want to travel to Japan", result.getContent().get(1).getContent());
        assertEquals(MessageConstants.SENDER_AI, result.getContent().get(1).getSender());

        verify(jwtUtil, times(1)).decodeToken(validToken);
        verify(messageRepository, times(1)).findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    @Test
    void getMessages_EmptyResult_ReturnsEmptyPageWithCorrectPagination() {
        Page<Message> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(jwtUtil.decodeToken(validToken)).thenReturn(claims);
        when(messageRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable))
                .thenReturn(emptyPage);

        Page<MessageResponseDto> result = messageService.getMessages(validToken, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        verify(jwtUtil, times(1)).decodeToken(validToken);
        verify(messageRepository, times(1)).findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    @Test
    void createMessages_ValidRequest_SavesMessagesAndReturnsAiResponse() {
        try (MockedStatic<UuidUtil> mockedUuidUtil = mockStatic(UuidUtil.class)) {
            mockedUuidUtil.when(() -> UuidUtil.generateUniqueId(any()))
                    .thenReturn("msg-user-123")
                    .thenReturn("msg-ai-123");

            when(jwtUtil.decodeToken(validToken)).thenReturn(claims);

            MessageResponseDto result = messageService.createMessages(messageRequestDto);

            assertNotNull(result);
            assertEquals(MessageConstants.AI_ADVICE_PREFIX + "I want to travel to Japan", result.getContent());
            assertEquals(MessageConstants.SENDER_AI, result.getSender());
            assertNotNull(result.getCreatedDateTime());

            verify(jwtUtil, times(1)).decodeToken(validToken);
            verify(messageRepository, times(2)).saveAndFlush(any(Message.class));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"JWT token has expired", "JWT token signature is invalid"})
    void createMessages_InvalidToken_ThrowsInvalidJwtTokenException(String errorMessage) {
        when(jwtUtil.decodeToken(validToken))
                .thenThrow(new InvalidJwtTokenException(errorMessage));

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            messageService.createMessages(messageRequestDto));

        assertEquals(errorMessage, exception.getMessage());

        verify(jwtUtil, times(1)).decodeToken(validToken);
        verify(messageRepository, never()).saveAndFlush(any(Message.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"What are the best beaches in Thailand?", "Recommend restaurants in Paris", "Best hiking trails in Switzerland"})
    void createMessages_DifferentContent_GeneratesCorrectAiResponse(String content) {
        MessageRequestDto customRequest = MessageRequestDto.builder()
                .content(content)
                .token(validToken)
                .build();

        try (MockedStatic<UuidUtil> mockedUuidUtil = mockStatic(UuidUtil.class)) {
            mockedUuidUtil.when(() -> UuidUtil.generateUniqueId(any()))
                    .thenReturn("msg-custom-123")
                    .thenReturn("msg-ai-custom-123");

            when(jwtUtil.decodeToken(validToken)).thenReturn(claims);

            MessageResponseDto result = messageService.createMessages(customRequest);

            assertNotNull(result);
            assertEquals(MessageConstants.AI_ADVICE_PREFIX + content, result.getContent());
            assertEquals(MessageConstants.SENDER_AI, result.getSender());

            verify(jwtUtil, times(1)).decodeToken(validToken);
            verify(messageRepository, times(2)).saveAndFlush(any(Message.class));
        }
    }

    @Test
    void createMessages_RepositoryException_ThrowsException() {
        try (MockedStatic<UuidUtil> mockedUuidUtil = mockStatic(UuidUtil.class)) {
            mockedUuidUtil.when(() -> UuidUtil.generateUniqueId(any())).thenReturn("msg-123");

            when(jwtUtil.decodeToken(validToken)).thenReturn(claims);
            when(messageRepository.saveAndFlush(any(Message.class)))
                    .thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                messageService.createMessages(messageRequestDto));

            verify(jwtUtil, times(1)).decodeToken(validToken);
            verify(messageRepository, times(1)).saveAndFlush(any(Message.class));
        }
    }
}
