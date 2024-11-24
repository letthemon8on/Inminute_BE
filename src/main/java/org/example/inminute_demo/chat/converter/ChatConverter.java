package org.example.inminute_demo.chat.converter;

import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.domain.MessageType;
import org.example.inminute_demo.chat.dto.chat.request.ChatRequest;
import org.example.inminute_demo.chat.dto.chat.response.ChatResponse;
import org.example.inminute_demo.chat.dto.chat.response.ChatStatusResponse;
import org.example.inminute_demo.chat.dto.chat.response.ChatStopResponse;
import org.example.inminute_demo.chat.dto.flask.response.OneLineSummary;
import org.example.inminute_demo.chat.dto.flask.response.SummaryByMember;
import org.example.inminute_demo.chat.dto.gpt.response.ToDoResponse;

import java.util.List;

public class ChatConverter {

    public static Chat toChat(ChatRequest chatRequest, String username, String uuid) {
        return Chat.builder()
                .username(username)
                .type(chatRequest.type())
                .content(chatRequest.content())
                .uuid(uuid)
                .build();
    }

    public static ChatResponse toChatResponse(Chat chat, String username, String nickname) {
        return ChatResponse.builder()
                .id(chat.getId())
                .username(username)
                .nickname(nickname)
                .type(chat.getType())
                .createdAt(chat.getCreated_at())
                .content(chat.getContent())
                .build();
    }

    public static Chat toChatFromTranscript(String transcript, String username, String uuid) {
        return Chat.builder()
                .username(username)
                .type(MessageType.CHAT)
                .content(transcript)
                .uuid(uuid)
                .build();
    }

    public static ChatStatusResponse toChatStatusResponse(Boolean isStart, String username, String nickname) {
        return ChatStatusResponse.builder()
                .username(username)
                .nickname(nickname)
                .isStart(isStart)
                .build();
    }

    public static ChatStopResponse toChatStopResponse(OneLineSummary oneLineSummary, List<SummaryByMember> summaryByMemberList,
                                                      List<ToDoResponse> toDoResponseList) {
        return ChatStopResponse.builder()
                .summary(oneLineSummary.summary())
                .summaryByMemberList(summaryByMemberList)
                .toDoResponseList(toDoResponseList)
                .build();
    }
}
