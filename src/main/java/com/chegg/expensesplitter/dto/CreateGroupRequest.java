package com.chegg.expensesplitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateGroupRequest {

    @NotBlank
    private String name;

    @NotEmpty
    @Builder.Default
    private List<@NotBlank String> members = new ArrayList<>();
}
