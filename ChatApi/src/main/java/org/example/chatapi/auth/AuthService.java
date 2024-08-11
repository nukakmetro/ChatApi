package org.example.chatapi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatapi.auth.requests.AuthorizationRequest;
import org.example.chatapi.auth.requests.RegistrationRequest;
import org.example.chatapi.auth.responses.AuthorizationResponse;
import org.example.chatapi.auth.token.JwtService;
import org.example.chatapi.auth.token.Token;
import org.example.chatapi.auth.token.TokenRepo;
import org.example.chatapi.auth.token.TokenType;
import org.example.chatapi.exceptions.InvalidRequestException;
import org.example.chatapi.exceptions.UserIsBlockedException;
import org.example.chatapi.exceptions.UsernameIsExistException;
import org.example.chatapi.user.model.UserModel;
import org.example.chatapi.user.repo.RoleRepo;
import org.example.chatapi.user.repo.UserRepo;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final TokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; //UserNotFoundException

    public AuthorizationResponse registration(RegistrationRequest request) throws Exception {

        if (!request.isValid())
            throw new InvalidRequestException("Bad request");
        if (userRepo.existsByUsername(request.getUsername()))
            throw new UsernameIsExistException("Username: " + request.getUsername() + " is already exist");

        var user = UserModel.builder()
                .username(request.getUsername())
                .isActive(false)
                .isBlocked(false)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(roleRepo.findByName("ROLE_USER"))) // TODO: roles to enum
                .build();

        var savedUser = userRepo.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthorizationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthorizationResponse authorization(AuthorizationRequest request) throws Exception {
        if (!request.isValid())
            throw new InvalidRequestException("Bad request");

        var user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User " + request.getUsername() + " is not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        ); // TODO: разобраться с exception

        if (user.getIsBlocked()) throw new UserIsBlockedException("User is blocked");

        user.setIsActive(true);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthorizationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

//        throw new InvalidCredentialsException("Invalid credentials");
    }


    private void saveUserToken(UserModel user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens(UserModel user) {
        var validUserTokens = tokenRepo.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepo.findByUsername(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthorizationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
