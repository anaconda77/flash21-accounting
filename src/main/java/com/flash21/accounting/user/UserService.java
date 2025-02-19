package com.flash21.accounting.user;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * UserRegisterDto를 기반으로 회원을 등록합니다.
     * @param userRegisterDto UserRegisterDto
     * @return 등록된 user의 readDto
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserReadDto register(UserRegisterDto userRegisterDto) {

        if(userRepository.existsByUsername(userRegisterDto.username())){
           throw AccountingException.of(UserErrorCode.DUPLICATE_USERNAME);
        }

        String hashedPassword = bCryptPasswordEncoder.encode(userRegisterDto.password());
        User user = userRepository.save(userRegisterDto.toEntity(hashedPassword));

        return UserReadDto.fromEntity(user);
    }

    /**
     * userName을 기반으로 회원을 조회합니다.
     * @param username
     * @return 조횐된 user의 readDto
     */
    public UserReadDto findByUsername(String username) {

        User user = userRepository.findByUsername(username);

        if(user == null){
            throw AccountingException.of(UserErrorCode.USER_NOT_FOUND);
        }

        return UserReadDto.fromEntity(user);
    }

    /**
     * userUpdateDto를 기반으로 회원정보를 수정합니다.
     * @param userUpdateDto UserUpdateDto
     * @return 수정된 user의 readDto
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserReadDto updateUser(UserUpdateDto userUpdateDto) {

        User user = userRepository.findById(userUpdateDto.id())
                .orElseThrow(() -> AccountingException.of(UserErrorCode.USER_NOT_FOUND));


        String hashedPassword = bCryptPasswordEncoder.encode(userUpdateDto.password());
        user.updateUser(userUpdateDto, hashedPassword);

        return UserReadDto.fromEntity(user);
    }

    /**
     * username과 password를 기반으로 회원을 탈퇴합니다.
     * @param username String
     * @param password String
     * @return 삭제된 user의 id
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Long deleteUser(String username, String password) {

        User user = userRepository.findByUsername(username);

        if(user == null){
            throw AccountingException.of(UserErrorCode.USER_NOT_FOUND);
        }

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw AccountingException.of(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH);
        }

        userRepository.delete(user);

        return user.getId();
    }
}
