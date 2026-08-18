package com.kanban.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailStatusResponse {
    private boolean enabled;
    private boolean credentialsConfigured;
    private boolean ready;
    private String host;
    private String from;
    private String hint;
}
