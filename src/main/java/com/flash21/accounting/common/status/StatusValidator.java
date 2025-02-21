package com.flash21.accounting.common.status;

import com.flash21.accounting.common.exception.AccountingException;

import java.util.Arrays;
import java.util.function.Supplier;

public class StatusValidator {
    /**
     * 화면 표시 이름으로부터 해당 상태 값 찾기
     *
     * @param enumClass 찾을 열거형 클래스
     * @param displayName 화면 표시 이름
     * @param exceptionSupplier 찾지 못했을 때 발생시킬 예외 공급자
     * @return 찾은 열거형 값
     */
    public static <T extends Enum<T> & StatusType> T fromDisplayName(
            Class<T> enumClass,
            String displayName,
            Supplier<AccountingException> exceptionSupplier) {

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(status -> status.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(exceptionSupplier);
    }

    /**
     * 상태 변경 유효성 검사
     *
     * @param currentStatus 현재 상태
     * @param newStatus 새로운 상태
     * @param canceledException 취소 상태에서 변경 시 예외
     * @param invalidTransitionException 유효하지 않은 상태 변경 예외
     * @param doneContractException 완료 상태 계약 변경 예외
     */
    public static <T extends Enum<T> & StatusType> void validateStatusTransition(
            T currentStatus,
            T newStatus,
            AccountingException canceledException,
            AccountingException invalidTransitionException,
            AccountingException doneContractException) {

        // CANCELED 상태로의 변경은 항상 가능
        if (newStatus.name().equals("CANCELED")) {
            return;
        }

        // 현재 상태가 CANCELED면 상태 변경 불가
        if (currentStatus.name().equals("CANCELED")) {
            throw canceledException;
        }

        // 현재 상태별 가능한 다음 상태 검사
        switch (currentStatus.name()) {
            case "TEMPORARY":
                if (!newStatus.name().equals("ONGOING")) {
                    throw invalidTransitionException;
                }
                break;
            case "ONGOING":
                if (!newStatus.name().equals("DONE")) {
                    throw invalidTransitionException;
                }
                break;
            case "DONE":
                throw doneContractException;
            default:
                throw invalidTransitionException;
        }
    }
}
