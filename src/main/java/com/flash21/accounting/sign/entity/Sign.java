package com.flash21.accounting.sign.entity;

import com.flash21.accounting.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sign")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sign_id")
    private Integer signId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // FK (User)

    @Column(name = "sign_type", nullable = false, length = 10)
    private String signType;  // 결재 타입

    @Column(name = "sign_image", nullable = false, length = 255)
    private String signImage;  // 서명 이미지 URL 저장
}
