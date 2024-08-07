package org.example.chatapi.auth.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationResponse {
    private String message;
}
