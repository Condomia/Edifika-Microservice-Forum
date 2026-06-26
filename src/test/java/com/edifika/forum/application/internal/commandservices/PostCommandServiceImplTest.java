package com.edifika.forum.application.internal.commandservices;

import com.edifika.forum.domain.model.aggregates.Post;
import com.edifika.forum.infrastructure.persistence.jpa.repositories.PostRepository;
import com.edifika.forum.interfaces.rest.resources.CreatePostResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommandServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostCommandServiceImpl postCommandService;

    @Test
    void shouldCreatePostWhenResidentHasNoPostsToday() {

        CreatePostResource resource = mock(CreatePostResource.class);

        when(resource.title()).thenReturn("Aviso importante");
        when(resource.content()).thenReturn("Contenido del post");
        when(resource.residentId()).thenReturn(1L);

        String uploadedImageUrl = "https://storage.com/image.png";

        when(postRepository.findFirstByResidentIdOrderByCreatedAtDesc(1L))
                .thenReturn(Optional.empty());

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postCommandService.handle(resource, uploadedImageUrl);

        assertNotNull(result);
        assertEquals("Aviso importante", result.getTitle());
        assertEquals("Contenido del post", result.getContent());
        assertEquals(1L, result.getResidentId());
        assertEquals(uploadedImageUrl, result.getImageUrl());

        verify(postRepository, times(1))
                .findFirstByResidentIdOrderByCreatedAtDesc(1L);

        verify(postRepository, times(1))
                .save(any(Post.class));
    }

    @Test
    void shouldCreatePostWhenLastPostWasYesterday() {

        CreatePostResource resource = mock(CreatePostResource.class);

        when(resource.title()).thenReturn("Nuevo post");
        when(resource.content()).thenReturn("Contenido nuevo");
        when(resource.residentId()).thenReturn(1L);

        String uploadedImageUrl = "https://storage.com/new-image.png";

        Post lastPost = mock(Post.class);

        when(lastPost.getCreatedAt())
                .thenReturn(LocalDateTime.now().minusDays(1));

        when(postRepository.findFirstByResidentIdOrderByCreatedAtDesc(1L))
                .thenReturn(Optional.of(lastPost));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postCommandService.handle(resource, uploadedImageUrl);

        assertNotNull(result);
        assertEquals("Nuevo post", result.getTitle());
        assertEquals("Contenido nuevo", result.getContent());
        assertEquals(1L, result.getResidentId());
        assertEquals(uploadedImageUrl, result.getImageUrl());

        verify(postRepository, times(1))
                .findFirstByResidentIdOrderByCreatedAtDesc(1L);

        verify(postRepository, times(1))
                .save(any(Post.class));
    }

    @Test
    void shouldThrowExceptionWhenResidentAlreadyPostedToday() {

        CreatePostResource resource = mock(CreatePostResource.class);

        when(resource.residentId()).thenReturn(1L);

        String uploadedImageUrl = "https://storage.com/image.png";

        Post lastPost = mock(Post.class);

        when(lastPost.getCreatedAt())
                .thenReturn(LocalDate.now().atStartOfDay());

        when(postRepository.findFirstByResidentIdOrderByCreatedAtDesc(1L))
                .thenReturn(Optional.of(lastPost));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postCommandService.handle(resource, uploadedImageUrl)
        );

        assertEquals(
                "Restricción del mini foro: Cada usuario solo podrá realizar una publicación diaria.",
                exception.getMessage()
        );

        verify(postRepository, times(1))
                .findFirstByResidentIdOrderByCreatedAtDesc(1L);

        verify(postRepository, never())
                .save(any(Post.class));
    }

    @Test
    void shouldSavePostWithCorrectData() {

        CreatePostResource resource = mock(CreatePostResource.class);

        when(resource.title()).thenReturn("Título de prueba");
        when(resource.content()).thenReturn("Contenido de prueba");
        when(resource.residentId()).thenReturn(5L);

        String uploadedImageUrl = "https://storage.com/test-image.png";

        when(postRepository.findFirstByResidentIdOrderByCreatedAtDesc(5L))
                .thenReturn(Optional.empty());

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        postCommandService.handle(resource, uploadedImageUrl);

        ArgumentCaptor<Post> postCaptor =
                ArgumentCaptor.forClass(Post.class);

        verify(postRepository, times(1))
                .save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();

        assertEquals("Título de prueba", savedPost.getTitle());
        assertEquals("Contenido de prueba", savedPost.getContent());
        assertEquals(5L, savedPost.getResidentId());
        assertEquals(uploadedImageUrl, savedPost.getImageUrl());
    }
}