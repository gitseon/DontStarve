package com.example.dontstarve.src.user;

import com.example.dontstarve.src.ingredient.Ingredient;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 불완전 객체 생성 방지
@AllArgsConstructor
@Builder
@DynamicUpdate // 변경된 필드만 대응
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @Column(nullable = false, length = 20)
    private String name; // 이름

    @Column(nullable = false, length = 320, unique = true)
    @Pattern(regexp="[a-z0-9._%+-]{2,64}+@[[A-Z0-9.-]+\\.[a-z]]{2,255}$", message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email; // 이메일

    @Column(nullable = false, length = 255)
    private String password; // 비밀번호

    @Column(name = "createdAt",columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updatedAt", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일

    @Column(nullable = false, columnDefinition = "varchar(10) default 'active'")
    private String status; // 회원 상태

    @Column(nullable = false, columnDefinition = "varchar(10)")
    private String auth; // 회원 권한 - 관리자(ADMIN), 일반(USER)

    @Builder
    public User(String email, String password, String name, String auth) {
        this.email = email;
        this.password = "{bcrypt}" + password;
        this.name = name;
        this.auth = auth;
        this.status = "active";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        for(String role : auth.split(",")) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
