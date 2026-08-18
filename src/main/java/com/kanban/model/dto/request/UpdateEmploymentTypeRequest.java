package com.kanban.model.dto.request;

import com.kanban.model.enums.EmploymentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmploymentTypeRequest {

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;
}
