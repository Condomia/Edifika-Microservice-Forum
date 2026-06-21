package com.edifika.forum.interfaces.rest;

import com.edifika.forum.application.internal.commandservices.PostCommandServiceImpl;
import com.edifika.forum.domain.model.aggregates.Post;
import com.edifika.forum.domain.services.PostCommandService;
import com.edifika.forum.interfaces.rest.resources.CreatePostResource;
import com.edifika.forum.interfaces.rest.resources.PostResource;
import com.edifika.forum.interfaces.rest.transform.PostResourceFromEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostCommandService postCommandService;

    public PostController(PostCommandService postCommandService) {
        this.postCommandService = postCommandService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody @Valid CreatePostResource resource) {
        try {
            Post savedPost = postCommandService.handle(resource, resource.imageUrl());

            PostResource postResource = PostResourceFromEntityAssembler.toResourceFromEntity(savedPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(postResource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}