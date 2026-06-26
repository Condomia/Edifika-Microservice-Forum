package com.edifika.forum.interfaces.rest;

import com.edifika.forum.domain.model.aggregates.Post;
import com.edifika.forum.domain.services.PostCommandService;
import com.edifika.forum.interfaces.rest.resources.CreatePostResource;
import com.edifika.forum.interfaces.rest.resources.PostResource;
import com.edifika.forum.interfaces.rest.transform.PostResourceFromEntityAssembler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostCommandService postCommandService;

    @InjectMocks
    private PostController postController;

    @Test
    void shouldCreatePost() {

        CreatePostResource resource = mock(CreatePostResource.class);
        Post savedPost = mock(Post.class);
        PostResource postResource = mock(PostResource.class);

        when(resource.imageUrl())
                .thenReturn("https://storage.com/image.png");

        when(postCommandService.handle(resource, resource.imageUrl()))
                .thenReturn(savedPost);

        try (MockedStatic<PostResourceFromEntityAssembler> mockedAssembler =
                     mockStatic(PostResourceFromEntityAssembler.class)) {

            mockedAssembler
                    .when(() -> PostResourceFromEntityAssembler.toResourceFromEntity(savedPost))
                    .thenReturn(postResource);

            ResponseEntity<?> response = postController.createPost(resource);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertSame(postResource, response.getBody());

            verify(postCommandService, times(1))
                    .handle(resource, "https://storage.com/image.png");

            mockedAssembler.verify(
                    () -> PostResourceFromEntityAssembler.toResourceFromEntity(savedPost),
                    times(1)
            );
        }
    }

    @Test
    void shouldReturnBadRequestWhenResidentAlreadyPostedToday() {

        CreatePostResource resource = mock(CreatePostResource.class);

        String errorMessage =
                "Restricción del mini foro: Cada usuario solo podrá realizar una publicación diaria.";

        when(resource.imageUrl())
                .thenReturn("https://storage.com/image.png");

        when(postCommandService.handle(resource, resource.imageUrl()))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = postController.createPost(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());

        verify(postCommandService, times(1))
                .handle(resource, "https://storage.com/image.png");
    }
}