package com.flash21.accounting.file.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AttachmentFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long referenceId; // 외래키
    private String fileName = "myFile";
    @Column(nullable = false)
    private String fileSource;
    @Column(nullable = false, length = 20)
    private String fileContentType;
    @Column(nullable = false)
    private APINumber apinumber;
    private Integer typeId; // 거래처 첨부파일 구분 1: 사업자 등록증, 2: 통장 사본

    public AttachmentFile(Long referenceId, String fileName, String fileSource,
        String fileContentType,
        APINumber apinumber, Integer typeId) {
        this.referenceId = referenceId;
        this.fileName = fileName;
        this.fileSource = fileSource;
        this.fileContentType = fileContentType;
        this.apinumber = apinumber;
        this.typeId = typeId;
    }

}
