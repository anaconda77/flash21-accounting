package com.flash21.accounting.sign.controller;

import com.flash21.accounting.sign.dto.SignRequestDto;
import com.flash21.accounting.sign.dto.SignResponseDto;
import com.flash21.accounting.sign.service.SignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sign")
public class SignController {

    private final SignService signService;

    // 서명 등록
    @PostMapping
    public ResponseEntity<SignResponseDto> createSign(@Valid @RequestBody SignRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(signService.createSign(requestDto));
    }

    // 특정 서명 조회
    @GetMapping("/{signId}")
    public ResponseEntity<SignResponseDto> getSignById(@PathVariable Integer signId) {
        return ResponseEntity.ok(signService.getSignById(signId));
    }

    // 모든 서명 조회
    @GetMapping
    public ResponseEntity<List<SignResponseDto>> getAllSigns() {
        return ResponseEntity.ok(signService.getAllSigns());
    }

    // 서명 수정
    @PutMapping("/{signId}")
    public ResponseEntity<SignResponseDto> updateSign(
            @PathVariable Integer signId, @Valid @RequestBody SignRequestDto requestDto) {
        return ResponseEntity.ok(signService.updateSign(signId, requestDto));
    }

    // 서명 삭제
    @DeleteMapping("/{signId}")
    public ResponseEntity<Void> deleteSign(@PathVariable Integer signId) {
        signService.deleteSign(signId);
        return ResponseEntity.noContent().build();
    }
}
