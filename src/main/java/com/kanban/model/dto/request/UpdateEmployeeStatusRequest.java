package com.kanban.model.dto.request;

import com.kanban.model.enums.EmployeeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeStatusRequest {

    @NotNull(message = "Employee status is required")
    private EmployeeStatus employeeStatus;
}
