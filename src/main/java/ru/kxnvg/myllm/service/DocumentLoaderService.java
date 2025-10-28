package ru.kxnvg.myllm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.kxnvg.myllm.entity.LoadedDocument;
import ru.kxnvg.myllm.entity.enums.DocumentType;
import ru.kxnvg.myllm.exception.HashCalculateException;
import ru.kxnvg.myllm.exception.LoadDocumentException;
import ru.kxnvg.myllm.repository.LoadedDocumentRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentLoaderService implements CommandLineRunner {

    private final LoadedDocumentRepository documentRepository;
    private final ResourcePatternResolver resolver;
    private final VectorStore vectorStore;

    @Value("${app.document.chunk-size:500}")
    private int chunkSize;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting document loading process...");
        loadDocument();
    }

    public void loadDocument() {
        try {
            List<Resource> resources = Arrays.stream(
                    resolver.getResources("classpath:/knowledgebase/**/*.txt")
            ).toList();

            resources.stream()
                    .map(r -> Pair.of(r, calcContentHash(r)))
                    .filter(pair -> !documentRepository.existsByFilenameAndContentHash(pair.getFirst().getFilename(), pair.getSecond()))
                    .forEach(pair -> {
                        Resource resource = pair.getFirst();
                        List<Document> chunks = buildChunks(resource);

                        LoadedDocument document = LoadedDocument.builder()
                                .filename(resource.getFilename())
                                .contentHash(pair.getSecond())
                                .chunkCount(chunks.size())
                                .documentType(DocumentType.TXT)
                                .build();
                        documentRepository.save(document);
                        log.info("Loaded document: {} with {} chunks saved in DB", resource.getFilename(), chunks.size());
                    });

        } catch (IOException e) {
            log.error("Failed to load documents", e);
            throw new LoadDocumentException(e.getMessage());
        }
    }

    private String calcContentHash(Resource resource) {
        try {
            return DigestUtils.md5DigestAsHex(resource.getInputStream());
        } catch (IOException e) {
            log.error("Failed to calculate content hash for resource: {}", resource.getFilename(), e);
            throw new HashCalculateException(e.getMessage());
        }
    }

    private List<Document> buildChunks(Resource resource) {
        List<Document> documents = new TextReader(resource).get();
        var textSplitter = TokenTextSplitter.builder()
                .withChunkSize(chunkSize)
                .build();

        List<Document> chunks = textSplitter.apply(documents);
        vectorStore.accept(chunks);
        return chunks;
    }
}
