package org.example.inminute_demo.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.dto.response.ChatResponse;
import org.example.inminute_demo.chat.dto.response.ChatResponses;
import org.example.inminute_demo.chat.dto.response.ChatsInNote;
import org.example.inminute_demo.chat.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping(value = "/notes/{noteId}/chats", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ChatsInNote> getChattingList(
            @PathVariable(name = "noteId") Long noteId,
            @PageableDefault(sort = "createdAt") Pageable pageable) {

        ChatsInNote result = chatService.getByNoteId(noteId, pageable);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping(value = "/notes/{noteId}/chats/all", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ChatResponses> getAllChattingList(
            @PathVariable(name = "noteId") Long noteId) {

        List<ChatResponse> result = chatService.getAllByNoteId(noteId);

        ChatResponses chatResponses = new ChatResponses(result);

        return ApiResponse.onSuccess(chatResponses);
    }

    // 특정 채팅방(noteId)로부터 메시지를 받아 저장하고, 저장된 메시지를 WebSocket을 통해 구독자들에게 전송
    @MessageMapping("/chat.sendMessage/{noteId}")
    @SendTo("/topic/public/{noteId}") // /topic/public/{noteId} 경로를 구독하는 클라이언트들에게 메세지 전달
    public ChatResponse sendMessage(@DestinationVariable Long noteId,
                                    @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                    @Payload ChatRequest chatRequest) {

        return chatService.save(chatRequest, noteId, simpSessionAttributes);
    }
}