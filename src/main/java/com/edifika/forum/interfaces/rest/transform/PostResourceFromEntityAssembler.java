package com.edifika.forum.interfaces.rest.transform;

import com.edifika.forum.domain.model.aggregates.Post;
import com.edifika.forum.interfaces.rest.resources.PostResource;

public class PostResourceFromEntityAssembler {

    public static PostResource toResourceFromEntity(Post entity) {
        if (entity == null) return null;

        return new PostResource(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getResidentId(),
                entity.getImageUrl(),
                entity.getCreatedAt()
        );
    }
}