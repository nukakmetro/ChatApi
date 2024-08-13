package org.example.chatapi.auth.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationResponse {
    @JsonProperty("access_token")
    private String refreshToken;
    @JsonProperty("refresh_token")
    private String accessToken;
    @JsonProperty("message")
    private String message;
}
