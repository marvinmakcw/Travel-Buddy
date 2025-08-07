package com.hkust.smart_buddy.chatroom.controller;

import com.hkust.smart_buddy.chatroom.dto.MessageRequestDto;
import com.hkust.smart_buddy.chatroom.dto.MessageResponseDto;
import com.hkust.smart_buddy.chatroom.service.MessageService;
import com.hkust.smart_buddy.common.exception.InvalidJwtTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MessageRequestDto messageRequestDto;
    private MessageResponseDto messageResponseDto;
    private String validToken;
    private Pageable pageable;
    private Page<MessageResponseDto> messagePage;

    @BeforeEach
    void setUp() {
        validToken = "valid-jwt-token";

        messageRequestDto = MessageRequestDto.builder()
                .content("Hello, I need travel advice")
                .token(validToken)
                .build();

        messageResponseDto = MessageResponseDto.builder()
                .content("Ai Advice for: Hello, I need travel advice")
                .sender("A")
                .createdDateTime(LocalDateTime.now())
                .build();

        pageable = PageRequest.of(0, 10);

        List<MessageResponseDto> messageList = Arrays.asList(
                MessageResponseDto.builder()
                        .content("Hello, I need travel advice")
                        .sender("U")
                        .createdDateTime(LocalDateTime.now().minusMinutes(2))
                        .build(),
                MessageResponseDto.builder()
                        .content("Ai Advice for: Hello, I need travel advice")
                        .sender("A")
                        .createdDateTime(LocalDateTime.now().minusMinutes(1))
                        .build()
        );

        messagePage = new PageImpl<>(messageList, pageable, messageList.size());
    }

    @Test
    void getMessages_ValidToken_ReturnsMessagesSuccessfully() {
        when(messageService.getMessages(validToken, pageable))
                .thenReturn(messagePage);

        ResponseEntity<Page<MessageResponseDto>> response = messageController.getMessages(validToken, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals("Hello, I need travel advice", response.getBody().getContent().getFirst().getContent());
        assertEquals("U", response.getBody().getContent().getFirst().getSender());

        verify(messageService, times(1)).getMessages(validToken, pageable);
    }

    @Test
    void getMessages_EmptyResult_ReturnsEmptyPage() {
        Page<MessageResponseDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(messageService.getMessages(validToken, pageable))
                .thenReturn(emptyPage);

        ResponseEntity<Page<MessageResponseDto>> response = messageController.getMessages(validToken, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        assertEquals(0, response.getBody().getTotalElements());

        verify(messageService, times(1)).getMessages(validToken, pageable);
    }

    @Test
    void createMessages_ValidRequest_ReturnsMessageSuccessfully() {
        when(messageService.createMessages(messageRequestDto))
                .thenReturn(messageResponseDto);

        ResponseEntity<MessageResponseDto> response = messageController.createMessages(messageRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ai Advice for: Hello, I need travel advice", response.getBody().getContent());
        assertEquals("A", response.getBody().getSender());
        assertNotNull(response.getBody().getCreatedDateTime());

        verify(messageService, times(1)).createMessages(messageRequestDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"JWT token has expired", "JWT token signature is invalid", "JWT token is malformed"})
    void createMessages_InvalidToken_ThrowsInvalidJwtTokenException(String errorMessage) {
        when(messageService.createMessages(messageRequestDto))
                .thenThrow(new InvalidJwtTokenException(errorMessage));

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            messageController.createMessages(messageRequestDto));

        assertEquals(errorMessage, exception.getMessage());
        verify(messageService, times(1)).createMessages(messageRequestDto);
    }

    @Test
    void createMessages_NullToken_ThrowsValidationException() {
        MessageRequestDto invalidRequest = MessageRequestDto.builder()
                .content("Valid content")
                .token(null)
                .build();

        when(messageService.createMessages(invalidRequest))
                .thenThrow(new IllegalArgumentException("Token cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
            messageController.createMessages(invalidRequest));
    }

    @ParameterizedTest
    @ValueSource(strings = {"JWT token has expired", "JWT token cannot be null or empty"})
    void getMessages_InvalidToken_ThrowsInvalidJwtTokenException(String errorMessage) {
        when(messageService.getMessages(anyString(), eq(pageable)))
                .thenThrow(new InvalidJwtTokenException(errorMessage));

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            messageController.getMessages("invalid-token", pageable));

        assertEquals(errorMessage, exception.getMessage());
        verify(messageService, times(1)).getMessages(anyString(), eq(pageable));
    }
}
