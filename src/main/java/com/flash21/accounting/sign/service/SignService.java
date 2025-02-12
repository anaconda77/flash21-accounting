package com.flash21.accounting.sign.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.SignErrorCode;
import com.flash21.accounting.sign.dto.SignRequestDto;
import com.flash21.accounting.sign.dto.SignResponseDto;
import com.flash21.accounting.sign.entity.Sign;
import com.flash21.accounting.sign.repository.SignRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {

    private final SignRepository signRepository;
    private final UserRepository userRepository;

    // 서명 등록
    @Transactional
    public SignResponseDto createSign(SignRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new AccountingException(SignErrorCode.USER_NOT_FOUND));

        Sign sign = signRepository.save(Sign.builder()
                .user(user)
                .signType(requestDto.getSignType())
                .signImage(requestDto.getSignImage())
                .build());

        return SignResponseDto.from(sign);
    }

    // 서명 조회 (ID 기준)
    public SignResponseDto getSignById(Integer signId) {
        Sign sign = signRepository.findById(signId)
                .orElseThrow(() -> new AccountingException(SignErrorCode.SIGN_NOT_FOUND));
        return SignResponseDto.from(sign);
    }

    // 모든 서명 조회
    public List<SignResponseDto> getAllSigns() {
        return signRepository.findAll()
                .stream()
                .map(SignResponseDto::from)
                .collect(Collectors.toList());
    }

    // 서명 수정
    @Transactional
    public SignResponseDto updateSign(Integer signId, SignRequestDto requestDto) {
        Sign sign = signRepository.findById(signId)
                .orElseThrow(() -> new AccountingException(SignErrorCode.SIGN_NOT_FOUND));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new AccountingException(SignErrorCode.USER_NOT_FOUND));

        sign.setUser(user);
        sign.setSignType(requestDto.getSignType());
        sign.setSignImage(requestDto.getSignImage());

        return SignResponseDto.from(sign);
    }

    // 서명 삭제
    @Transactional
    public void deleteSign(Integer signId) {
        if (!signRepository.existsById(signId)) {
            throw new AccountingException(SignErrorCode.SIGN_NOT_FOUND);
        }
        signRepository.deleteById(signId);
    }
}
