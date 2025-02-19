package com.flash21.accounting.contract.entity;

public enum ProcessStatus {
    PENDING_APPROVAL, // 결재진행
    CONTRACTED, // 계약진행
    WAITING, // 작업대기
    BILLING, // 청구(납품)
    AWAITING_PAYMENT, // 입금 후 진행
    DONE // 최종완료
}