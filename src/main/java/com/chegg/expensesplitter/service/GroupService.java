package com.chegg.expensesplitter.service;

import com.chegg.expensesplitter.dto.CreateGroupRequest;
import com.chegg.expensesplitter.dto.GroupResponse;
import com.chegg.expensesplitter.exception.GroupNotFoundException;
import com.chegg.expensesplitter.model.Group;
import com.chegg.expensesplitter.repository.GroupRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        Group group = Group.builder()
                .name(request.getName())
                .members(new ArrayList<>(request.getMembers()))
                .build();

        return mapToResponse(groupRepository.save(group));
    }

    public List<GroupResponse> getGroups() {
        return groupRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public GroupResponse getGroup(Long id) {
        return groupRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));
    }

    private GroupResponse mapToResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .members(new ArrayList<>(group.getMembers()))
                .createdAt(group.getCreatedAt())
                .build();
    }
}
