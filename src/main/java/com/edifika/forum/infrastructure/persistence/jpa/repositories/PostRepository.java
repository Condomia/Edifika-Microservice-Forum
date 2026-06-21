package com.edifika.forum.infrastructure.persistence.jpa.repositories;

import com.edifika.forum.domain.model.aggregates.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Busca el último post de un residente para validar la restricción del informe
    Optional<Post> findFirstByResidentIdOrderByCreatedAtDesc(Long residentId);
}