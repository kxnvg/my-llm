package ru.kxnvg.myllm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kxnvg.myllm.entity.LoadedDocument;

public interface LoadedDocumentRepository extends JpaRepository<LoadedDocument, Long> {

    boolean existsByFilenameAndContentHash(String filename, String contentHash);
}
