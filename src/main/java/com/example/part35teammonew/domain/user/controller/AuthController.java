package com.example.part35teammonew.domain.user.controller;

import com.example.part35teammonew.domain.user.dto.TokenDto;
import com.example.part35teammonew.domain.user.dto.UserLoginRequest;
import com.example.part35teammonew.jwt.JwtFilter;
import com.example.part35teammonew.jwt.TokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/users/login")
public class AuthController {
  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @PostMapping(consumes = "application/json", produces = "application/json")
  public ResponseEntity<TokenDto> login(@Valid @RequestBody UserLoginRequest request){

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.createToken(authentication);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

    return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);

  }

}

