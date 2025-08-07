package com.hkust.smart_buddy.chatroom.controller;

import com.hkust.smart_buddy.chatroom.dto.MessageRequestDto;
import com.hkust.smart_buddy.chatroom.dto.MessageResponseDto;
import com.hkust.smart_buddy.chatroom.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smart_buddy/chatroom")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/chat-history")
    public ResponseEntity<Page<MessageResponseDto>> getMessages(@RequestParam String token, Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessages(token, pageable));
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponseDto> createMessages(@Valid @RequestBody MessageRequestDto requestDto) {
        MessageResponseDto createdMessage = messageService.createMessages(requestDto);
        return ResponseEntity.ok(createdMessage);
    }
}
