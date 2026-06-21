package com.edifika.forum.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostResource(
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
        String title,

        @NotBlank(message = "El contenido es obligatorio")
        @Size(min = 10, max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
        String content,

        Long residentId,

        String imageUrl
) {}