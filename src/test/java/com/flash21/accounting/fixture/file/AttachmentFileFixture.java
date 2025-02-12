package com.flash21.accounting.fixture.file;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.file.domain.AttachmentFile;

public class AttachmentFileFixture {

    public static AttachmentFile createDefault() {
        return new AttachmentFile(
            1L,
            "demo",
            System.getProperty("user.home") + "/accounting/files/demo.png",
            "image/png",
            APINumber.CONTRACT,
            null
        );
    }

}
