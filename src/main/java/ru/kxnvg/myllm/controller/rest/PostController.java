package ru.kxnvg.myllm.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kxnvg.myllm.model.dto.ModerationResult;
import ru.kxnvg.myllm.model.dto.PostDto;
import ru.kxnvg.myllm.service.PostService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ModerationResult createPost(@RequestBody PostDto postDto) {
        log.info("Received new post with title: {} and content: {}", postDto.title(), postDto.content());
        return postService.createPost(postDto);
    }
}
