package com.flash21.accounting.file.repository;

import com.flash21.accounting.file.domain.AttachmentFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, Long> {

    List<AttachmentFile> findByReferenceId(Long referenceId);
}
