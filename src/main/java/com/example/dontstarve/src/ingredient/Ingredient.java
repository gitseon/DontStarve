package com.example.dontstarve.src.ingredient;

import com.example.dontstarve.src.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 불완전 객체 생성 방지
@AllArgsConstructor
@Builder
@DynamicUpdate // 변경된 필드만 대응
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

/*    @Column
    private int userId; // 유저 식별자 - User와 1 : N 관계*/
    
    @Column(nullable = false, length = 100)
    private String name; // 식재료 이름
    
    @Column(nullable = false, length = 45)
    private String type; // 식재료 종류
    
    @Column(nullable = true, columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Column(name = "createdAt",columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updatedAt", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일
}
