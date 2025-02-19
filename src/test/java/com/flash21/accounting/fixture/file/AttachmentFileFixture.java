package com.flash21.accounting.fixture.file;

import com.flash21.accounting.file.domain.APINumber;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import com.flash21.accounting.file.domain.AttachmentFile;

public class AttachmentFileFixture {

    public static AttachmentFile createDefault() {
        String os = System.getProperty("os.name").toLowerCase();
        String fileSource;
        if (os.contains("mac")) {
            fileSource =  System.getProperty("user.home") + "/accounting/files/";
        } else if (os.contains("linux")) {
            fileSource =  "/var/accounting/files/";
        } else if (os.contains("windows")) {
            fileSource =  System.getProperty("user.home") + "\\accounting\\files\\";
        } else {
            throw AccountingException.of(FileErrorCode.UNSUPPORTED_OS);
        }


        return new AttachmentFile(
            1L,
            "demo",
            fileSource + "demo.png",
            "image/png",
            APINumber.CONTRACT,
            null
        );
    }

}
