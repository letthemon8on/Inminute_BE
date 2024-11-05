package org.example.inminute_demo.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.chat.dto.request.*;
import org.example.inminute_demo.chat.dto.response.*;
import org.example.inminute_demo.chat.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Chat", description = "채팅 관련 API입니다.")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

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

    // 바이트 코드로 인코딩된 오디오 데이터를 받아 텍스트로 변환 후 구독자들에게 전송
    @MessageMapping("/chat.sendAudio/{uuid}")
    @SendTo("/topic/public/{uuid}") // /topic/public/{uuid} 경로를 구독하는 클라이언트들에게 변환된 텍스트 메세지 전달
    public ChatResponse sendAudioMessage(@DestinationVariable String uuid,
                                         @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                         @Payload AudioRequest audioRequest) {

        return chatService.transcribe(audioRequest, uuid, simpSessionAttributes);
    }

    // 청크 단위로 인코딩된 오디오 데이터를 받아 병합한 후 텍스트로 변환하여 구독자들에게 전송
    @MessageMapping("/chat.sendAudioByChunk/{uuid}")
    @SendTo("/topic/public/{uuid}") // /topic/public/{uuid} 경로를 구독하는 클라이언트들에게 변환된 텍스트 메세지 전달
    public ChatResponse sendAudioChunk(@DestinationVariable String uuid,
                                         @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                         @Payload AudioChunkRequest audioChunkRequest) {

        return chatService.transcribeByChunk(audioChunkRequest, uuid, simpSessionAttributes);
    }

    // chatId로 채팅내역을 조회해 content 갱신 후 save
    @MessageMapping("/chat.update/{uuid}")
    @SendTo("/topic/public/{uuid}")
    public ChatResponse updateChat(@DestinationVariable String uuid,
                                   @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                   @Payload ChatUpdateRequest chatUpdateRequest) {

        return chatService.update(chatUpdateRequest, simpSessionAttributes);
    }

    // 회의 시작 버튼 클릭 -> 모든 참여자들에게 회의 시작 여부 Broadcasting
    @MessageMapping("/chat.start/{uuid}")
    @SendTo("/topic/public/{uuid}")
    public ChatStatusResponse startChatting(@DestinationVariable String uuid,
                                            @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                            @Payload ChatStartRequest chatStartRequest) {

        return chatService.startChatting(uuid, simpSessionAttributes);
    }

    // 회의 종료 시 호출 -> 추후에 Flask, GPT 적용하여 리팩토링 필요
    /*@MessageMapping("/chat.stop/{uuid}")
    @SendTo("/topic/public/{uuid}")
    public ChatStatusResponse stopChatting(@DestinationVariable String uuid,
                                         @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                         @Payload ChatStopRequest chatStopRequest) {

        return chatService.stopChatting(uuid, simpSessionAttributes);
    }*/
    @PostMapping("/notes/{uuid}/stop")
    @Operation(summary = "회의 종료", description = "회의를 종료하고 한 줄 요약을 반환합니다." +
            "<br> 화자별 요약 및 to do는 추가될 예정입니다. 헤헤")
    public ApiResponse<ChatStopResponse> stopChatting(@PathVariable(name = "uuid") String uuid) {

        return ApiResponse.onSuccess(chatService.stopChatting(uuid));
    }
}