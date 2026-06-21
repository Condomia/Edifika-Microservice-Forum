package com.edifika.forum.application.internal.commandservices;

import com.edifika.forum.domain.model.aggregates.Post;
import com.edifika.forum.domain.services.PostCommandService; // 👈 IMPORTAMOS TU NUEVA INTERFAZ
import com.edifika.forum.infrastructure.persistence.jpa.repositories.PostRepository;
import com.edifika.forum.interfaces.rest.resources.CreatePostResource;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;

    public PostCommandServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post handle(CreatePostResource resource, String uploadedImageUrl) {
        // Tu lógica exacta y perfecta de la restricción diaria:
        postRepository.findFirstByResidentIdOrderByCreatedAtDesc(resource.residentId())
                .ifPresent(lastPost -> {
                    LocalDate lastPostDate = lastPost.getCreatedAt().toLocalDate();
                    if (lastPostDate.equals(LocalDate.now())) {
                        throw new IllegalArgumentException("Restricción del mini foro: Cada usuario solo podrá realizar una publicación diaria.");
                    }
                });

        Post post = new Post();
        post.setTitle(resource.title());
        post.setContent(resource.content());
        post.setResidentId(resource.residentId());
        post.setImageUrl(uploadedImageUrl);

        return postRepository.save(post);
    }
}