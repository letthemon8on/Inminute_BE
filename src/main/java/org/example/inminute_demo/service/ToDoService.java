package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.domain.ToDo;
import org.example.inminute_demo.dto.noteJoinMember.response.NoteJoinMemberResponse;
import org.example.inminute_demo.repository.ToDoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.inminute_demo.dto.noteJoinMember.response.NoteJoinMemberResponse.*;

@Service
@RequiredArgsConstructor
public class ToDoService {

    private final ToDoRepository toDoRepository;

    public List<ToDoResponse> findAll(Long id) {

        List<ToDo> toDoList = toDoRepository.findAllByNoteJoinMember_Id(id);

        return toDoList.stream()
                .map(toDo -> ToDoResponse.builder()
                        .toDoId(toDo.getId())
                        .content(toDo.getContent())
                        .isDone(toDo.getIsDone())
                        .build())
                .collect(Collectors.toList());
    }
}
