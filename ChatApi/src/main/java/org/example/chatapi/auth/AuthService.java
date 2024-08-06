package org.example.chatapi.auth;

import lombok.RequiredArgsConstructor;
import org.example.chatapi.auth.requests.AuthorizationRequest;
import org.example.chatapi.auth.requests.RegistrationRequest;
import org.example.chatapi.auth.responses.AuthorizationResponse;
import org.example.chatapi.auth.responses.RegistrationResponse;
import org.example.chatapi.exceptions.InvalidCredentialsException;
import org.example.chatapi.exceptions.InvalidRequestException;
import org.example.chatapi.exceptions.UsernameIsExistException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public RegistrationResponse registration(RegistrationRequest request) throws Exception {
        // TODO: registration
        throw new UsernameIsExistException("Username: " + request.getUsername() + " is already exist");
        throw new InvalidRequestException("Bad request");
    }

    public AuthorizationResponse authorization(AuthorizationRequest request) throws Exception {
        // TODO: authorization
        throw new InvalidRequestException("Bad request");
        throw new UsernameNotFoundException("Username: " + request.getUsername() + " is not found");
        throw new InvalidCredentialsException("Invalid credentials");
    }
}
