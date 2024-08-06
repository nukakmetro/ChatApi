package org.example.chatapi.auth.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequest {
    private String email;
    private String username;
    private String name;
    private String lastname;
    private String password;
}
