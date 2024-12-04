package org.example.cargo29.repository;

import org.example.cargo29.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    @Query("SELECT b FROM BlogPost b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:publicationDate IS NULL OR b.publicationDate = :publicationDate) AND " +
            "(:content IS NULL OR b.content LIKE %:content%)")
    List<BlogPost> search(@Param("title") String title,
                          @Param("publicationDate") LocalDate publicationDate,
                          @Param("content") String content);
}