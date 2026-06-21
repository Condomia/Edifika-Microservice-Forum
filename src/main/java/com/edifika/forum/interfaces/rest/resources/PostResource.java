package com.edifika.forum.interfaces.rest.resources;

import java.time.LocalDateTime;

public record PostResource(
        Long id,
        String title,
        String content,
        Long residentId,
        String imageUrl,
        LocalDateTime createdAt
) {}