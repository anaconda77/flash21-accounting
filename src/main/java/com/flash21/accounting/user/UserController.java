package com.flash21.accounting.user;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.UserErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Tag(name = "계정", description = "회원가입 및 로그인 API")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "사용자는 회원가입을 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "409", description = "중복된 아이디의 회원이 존재함")
    })
    public ResponseEntity<UserReadDto> register(
            @Valid @RequestBody UserRegisterDto requestBody
    ) {
        UserReadDto responseBody = userService.register(requestBody);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @GetMapping("/user/check-duplicate")
    public ResponseEntity<Void> checkDuplicate(@RequestParam String username) {

        User user = userRepository.findByUsername(username);
        if (user != null) {
            throw AccountingException.of(UserErrorCode.DUPLICATE_USERNAME);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * username을 기반으로 회원을 조회합니다.
     */
    @GetMapping("/user/{username}")
    @Operation(summary = "회원 조회", description = "username을 기반으로 회원 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    })
    public ResponseEntity<UserReadDto> findByUsername(@PathVariable String username) {
        UserReadDto responseBody = userService.findByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

    /**
     * 회원 정보를 수정합니다.
     */
    @PutMapping("/user/update")
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    })
    public ResponseEntity<UserReadDto> updateUser(
            @Valid @RequestBody UserUpdateDto userUpdateDto
    ) {
        UserReadDto responseBody = userService.updateUser(userUpdateDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/user/delete")
    @Operation(summary = "회원 탈퇴", description = "username과 password를 기반으로 회원 탈퇴를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    })
    public ResponseEntity<Long> deleteUser(
            @RequestParam String username,
            @RequestParam String password
    ) {
        Long responseBody = userService.deleteUser(username, password);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }
}
