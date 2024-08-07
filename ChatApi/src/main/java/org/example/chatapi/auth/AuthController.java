package org.example.chatapi.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatapi.auth.requests.AuthorizationRequest;
import org.example.chatapi.auth.requests.RegistrationRequest;
import org.example.chatapi.auth.responses.AuthorizationResponse;
import org.example.chatapi.auth.responses.RegistrationResponse;
import org.example.chatapi.exceptions.InvalidCredentialsException;
import org.example.chatapi.exceptions.InvalidRequestException;
import org.example.chatapi.exceptions.UsernameIsExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatApi/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registration(@RequestBody RegistrationRequest request) {

        RegistrationResponse response = RegistrationResponse.builder().build();
        HttpStatus status;
        String message;

        try {
            response = service.registration(request);
            status = HttpStatus.CREATED;
            message = status.getReasonPhrase();
        } catch (UsernameIsExistException | InvalidRequestException e) {
            status = HttpStatus.BAD_REQUEST;
            message = e.getMessage();
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = e.getMessage();
        }

        response.setMessage(message);

        log.info("REGISTRATION - email: {}, status: {}, message: {}",
                request.getEmail(), status, message);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @PostMapping("/authorization")
    public ResponseEntity<AuthorizationResponse> authorization(@RequestBody AuthorizationRequest request) {

        AuthorizationResponse response = AuthorizationResponse.builder().build();
        HttpStatus status;
        String message;

        try {
            response = service.authorization(request);
            status = HttpStatus.CREATED;
            message = status.getReasonPhrase();
        } catch (UsernameNotFoundException | InvalidRequestException |
                 InvalidCredentialsException e) {
            status = HttpStatus.BAD_REQUEST;
            message = e.getMessage();
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = e.getMessage();
        }

        response.setMessage(message);

        log.info("AUTHORIZATION - username: {}, status: {}, message: {}",
                request.getUsername(), status, message);

        return ResponseEntity.status(status).body(response);
    }

}


