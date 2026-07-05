package com.chegg.expensesplitter.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {

    private Long id;

    private String name;

    @Builder.Default
    private List<String> members = new ArrayList<>();

    private LocalDateTime createdAt;
}
