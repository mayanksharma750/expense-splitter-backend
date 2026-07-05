package com.chegg.expensesplitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chegg.expensesplitter.dto.CreateGroupRequest;
import com.chegg.expensesplitter.dto.GroupResponse;
import com.chegg.expensesplitter.exception.GroupNotFoundException;
import com.chegg.expensesplitter.model.Group;
import com.chegg.expensesplitter.repository.GroupRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    void createGroupSavesGroupAndReturnsDto() {
        CreateGroupRequest request = CreateGroupRequest.builder()
                .name("Trip")
                .members(List.of("Alice", "Bob"))
                .build();
        Group savedGroup = Group.builder()
                .id(1L)
                .name("Trip")
                .members(List.of("Alice", "Bob"))
                .createdAt(LocalDateTime.now())
                .build();

        when(groupRepository.save(any(Group.class))).thenReturn(savedGroup);

        GroupResponse response = groupService.createGroup(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Trip");
        assertThat(response.getMembers()).containsExactly("Alice", "Bob");
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void getGroupThrowsWhenGroupDoesNotExist() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroup(99L))
                .isInstanceOf(GroupNotFoundException.class)
                .hasMessage("Group not found");
    }
}
