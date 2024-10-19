package org.example.inminute_demo.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Chat", description = "채팅 관련 API입니다.")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping(value = "/notes/{uuid}/chats", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "회의록 전체 채팅 조회(페이징)", description = "해당 회의록에 작성된 모든 채팅 내역을 페이징 처리하여 조회합니다.")
    public ApiResponse<ChatsInNote> getChattingList(@PathVariable(name = "uuid") String uuid,
                                                    @PageableDefault(sort = "createdAt") Pageable pageable) {

        ChatsInNote result = chatService.getByNoteUUID(uuid, pageable);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping(value = "/notes/{uuid}/chats/all", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "회의록 전체 채팅 조회", description = "해당 회의록에 작성된 모든 채팅 내역을 조회합니다.")
    public ApiResponse<ChatResponses> getAllChattingList(@PathVariable(name = "uuid") String uuid) {

        List<ChatResponse> result = chatService.getAllByNoteUUID(uuid);

        ChatResponses chatResponses = new ChatResponses(result);

        return ApiResponse.onSuccess(chatResponses);
    }

    // 특정 채팅방(uuid)로부터 메시지를 받아 저장하고, 저장된 메시지를 WebSocket을 통해 구독자들에게 전송
    @MessageMapping("/chat.sendMessage/{uuid}")
    @SendTo("/topic/public/{uuid}") // /topic/public/{uuid} 경로를 구독하는 클라이언트들에게 메세지 전달
    public ChatResponse sendMessage(@DestinationVariable String uuid,
                                    @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                    @Payload ChatRequest chatRequest) {

        return chatService.save(chatRequest, uuid, simpSessionAttributes);
    }
}