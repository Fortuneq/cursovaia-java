package org.example.cargo29.service;

import org.example.cargo29.entity.BlogPost;
import org.example.cargo29.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository repo;

    public List<BlogPost> listAll() {
        return repo.findAll();
    }

    public void save(BlogPost blogPost) {
        repo.save(blogPost);
    }

    public BlogPost get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<BlogPost> search(String title, LocalDate publicationDate, String content) {
        return repo.search(title, publicationDate, content);
    }
}