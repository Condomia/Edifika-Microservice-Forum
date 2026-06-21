package com.edifika.forum.domain.services;

import com.edifika.forum.domain.model.aggregates.Post;
import com.edifika.forum.interfaces.rest.resources.CreatePostResource;

public interface PostCommandService {
    // Definimos el contrato tal como lo hace reservas
    Post handle(CreatePostResource resource, String uploadedImageUrl);
}