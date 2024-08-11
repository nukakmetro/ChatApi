package org.example.chatapi.auth.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationRequest {

    private String username;
    private String password;

    public boolean isValid() {
        // TODO: implement validation
        return true;
    }
}
