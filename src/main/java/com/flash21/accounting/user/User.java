package com.flash21.accounting.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            length = 15
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            length = 70
    )
    private String password;

    @Column(
            name = "name",
            nullable = false,
            length = 15
    )
    private String name;

    @Column(
            name = "phone_number",
            nullable = false,
            length = 15
    )
    private String phoneNumber;

    @Column(
            name = "email",
            nullable = false,
            length = 30
    )
    private String email;

    @Column(
            name = "address",
            nullable = false,
            length = 30
    )
    private String address;

    @Column(
            name = "address_detail",
            nullable = false,
            length = 30
    )
    private String addressDetail;

    @Column(
            name = "role",
            nullable = false,
            length = 30
    )
    private Role role;

    @Column(
            name = "grade",
            nullable = false,
            length = 30
    )
    private String grade;

    @Column(
            name = "company_phone_number",
            nullable = false,
            length = 30
    )
    private String companyPhoneNumber;

    @Column(
            name = "company_fax_number",
            nullable = false,
            length = 30
    )
    private String companyFaxNumber;

    @Builder
    public User(String username, String password, String name, String phoneNumber, String email, String address, String addressDetail, Role role, String grade, String companyPhoneNumber, String companyFaxNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.addressDetail = addressDetail;
        this.role = role;
        this.grade = grade;
        this.companyPhoneNumber = companyPhoneNumber;
        this.companyFaxNumber = companyFaxNumber;
    }

    public void updateUser(UserUpdateDto userUpdateDto, String hashedPassword) {
        this.password = hashedPassword;
        this.name = userUpdateDto.name();
        this.phoneNumber = userUpdateDto.phoneNumber();
        this.email = userUpdateDto.email();
        this.address = userUpdateDto.address();
        this.addressDetail = userUpdateDto.addressDetail();
        this.role = userUpdateDto.role();
        this.grade = userUpdateDto.grade();
        this.companyPhoneNumber = userUpdateDto.companyPhoneNumber();
        this.companyFaxNumber = userUpdateDto.companyFaxNumber();
    }
}
