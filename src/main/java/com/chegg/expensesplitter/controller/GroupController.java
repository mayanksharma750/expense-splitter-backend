package com.chegg.expensesplitter.controller;

import com.chegg.expensesplitter.dto.CreateGroupRequest;
import com.chegg.expensesplitter.dto.GroupResponse;
import com.chegg.expensesplitter.service.GroupService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(request));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getGroups() {
        return ResponseEntity.ok(groupService.getGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }
}
