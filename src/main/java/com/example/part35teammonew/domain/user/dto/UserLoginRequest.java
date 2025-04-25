package com.example.part35teammonew.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginRequest {

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    String email;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Size(min = 6, max = 16, message = "비밀번호는 6자 이상 16자 이하여야 합니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    String password;
}
