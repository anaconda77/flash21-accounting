package com.flash21.accounting.detailcontract.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import lombok.Getter;

import java.util.Arrays;


public enum DetailContractCategory {
    // 계약서카테고리 추가

    // 웹사이트 카테고리
    WEBSITE_CONSTRUCTION,//웹사이트 구축
    WEBSITE_DESIGN,//디자인
    WEBSITE_DEVELOPMENT,//프로그램 개발
    WEBSITE_PUBLISHING, //퍼블리싱

    // 유지관리 카테고리
    MAINTENANCE_DESIGN, //디자인 수정
    MAINTENANCE_PROGRAM, //프로그램 수정
    MAINTENANCE_SERVICE, //유지관리
    MAINTENANCE_CERTIFICATION, //인증서
    MAINTENANCE_PROMOTION, //온라인 홍보

    // 영상 카테고리
    VIDEO_PRODUCTION, //홍보영상 제작

    // 인쇄물 카테고리
    PRINT_CATALOG_DESIGN, //카탈로그 디자인
    PRINT_PRODUCTION, //인쇄
    PRINT_OTHER, //기타

    // 호스팅 카테고리
    HOSTING_SERVICE; //호스팅

}
