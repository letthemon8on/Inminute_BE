package org.example.inminute_demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.chat.dto.gpt.response.CreateToDoResponse;
import org.example.inminute_demo.chat.dto.gpt.response.ToDoList;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.ToDo;
import org.example.inminute_demo.dto.toDo.request.UpdateToDoRequest;
import org.example.inminute_demo.dto.toDo.response.ToDoResponse;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.repository.ToDoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final MemberRepository memberRepository;

    public List<ToDoResponse> findAll(String uuid) {

        List<ToDo> toDoList = toDoRepository.findAllByUuid(uuid);

        return toDoList.stream()
                .map(toDo -> ToDoResponse.builder()
                        .id(toDo.getId())
                        .uuid(toDo.getUuid())
                        .username(toDo.getUsername())
                        .nickname(toDo.getNickname())
                        .content(toDo.getContent())
                        .isDone(toDo.getIsDone())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateToDo(Long toDoId, UpdateToDoRequest updateToDoRequest) {

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTE_NOT_FOUND));

        if (updateToDoRequest.content() != null) {
            toDo.updateContent(updateToDoRequest.content());
        }
        if (updateToDoRequest.isDone() != null) {
            toDo.updateIsDone(updateToDoRequest.isDone());
        }
        toDoRepository.save(toDo);
    }

    @Transactional
    public void saveToDo(List<CreateToDoResponse> createToDoResponses, String uuid) {

        for (CreateToDoResponse createToDoResponse : createToDoResponses) {
            for (ToDoList toDoList : createToDoResponse.toDoLists()) {
                Member member = memberRepository.findByUsername(createToDoResponse.username())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

                ToDo toDo = ToDo.builder()
                        .uuid(uuid)
                        .username(createToDoResponse.username())
                        .nickname(member.getNickname())
                        .content(toDoList.todo())
                        .isDone(false)
                        .build();

                toDoRepository.save(toDo);
            }
        }
    }
}
