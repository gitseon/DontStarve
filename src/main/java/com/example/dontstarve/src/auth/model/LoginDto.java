package com.example.dontstarve.src.auth.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {
    private String email;
    private String password;
}
