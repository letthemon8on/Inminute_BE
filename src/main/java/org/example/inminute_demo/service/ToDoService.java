package org.example.inminute_demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.ToDo;
import org.example.inminute_demo.dto.toDo.request.UpdateToDoRequest;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.ToDoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.inminute_demo.dto.noteJoinMember.response.NoteJoinMemberResponse.*;

@Service
@RequiredArgsConstructor
public class ToDoService {

    private final ToDoRepository toDoRepository;

    public List<ToDoResponse> findAll(Long toDoId) {

        List<ToDo> toDoList = toDoRepository.findAllByNoteJoinMember_Id(toDoId);

        return toDoList.stream()
                .map(toDo -> ToDoResponse.builder()
                        .toDoId(toDo.getId())
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
}
