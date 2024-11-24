package org.example.inminute_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.dto.toDo.request.UpdateToDoRequest;
import org.example.inminute_demo.dto.toDo.response.ToDoListResponse;
import org.example.inminute_demo.dto.toDo.response.ToDoResponse;
import org.example.inminute_demo.service.ToDoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TODO", description = "TODO 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/to-do")
public class ToDoController {

    private final ToDoService toDoService;

    @GetMapping("/{uuid}")
    @Operation(summary = "회의록 참여자별 TODO 리스트 조회", description = "회의록의 uuid를 통해 회의록 참여자별 TODO 리스트를 조회합니다.")
    public ApiResponse<ToDoListResponse> findAll(@PathVariable String uuid) {

        List<ToDoResponse> toDoResponses = toDoService.findAll(uuid);
        return ApiResponse.onSuccess(new ToDoListResponse(toDoResponses));
    }

    @PatchMapping("/{toDoId}")
    @Operation(summary = "회의록 참여자별 TODO 수정", description = "회의 참여자별 TODO 내용 또는 완료 여부를 수정합니다.")
    public ApiResponse<?> updateToDo(@PathVariable Long toDoId,
                                     @RequestBody UpdateToDoRequest updateToDoRequest) {
        toDoService.updateToDo(toDoId, updateToDoRequest);
        return ApiResponse.onSuccess("TODO 수정 완료됨");
    }
}
