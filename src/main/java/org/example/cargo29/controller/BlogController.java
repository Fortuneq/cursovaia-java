package org.example.cargo29.controller;

import org.example.cargo29.entity.BlogPost;
import org.example.cargo29.entity.User;
import org.example.cargo29.service.BlogPostService;
import org.example.cargo29.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPanel(Model model) {
        List<BlogPost> blogPosts = blogPostService.listAll();
        model.addAttribute("blogPosts", blogPosts);
        return "blog_admin";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newBlogPostForm(Model model) {
        model.addAttribute("blogPost", new BlogPost());
        return "new_blog_post";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String saveBlogPost(@ModelAttribute("blogPost") BlogPost blogPost) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User author = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());
        blogPost.setAuthor(author);
        blogPost.setPublicationDate(LocalDate.now());
        blogPostService.save(blogPost);
        return "Запись успешно сохранена!";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody BlogPost editBlogPostForm(@PathVariable("id") Long id) {
        BlogPost blogPost = blogPostService.get(id);
        return blogPost;
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String deleteBlogPost(@RequestParam("id") Long id) {
        blogPostService.delete(id);
        return "Запись успешно удалена!";
    }

    @GetMapping("/")
    public String blogHome(Model model) {
        List<BlogPost> blogPosts = blogPostService.listAll();
        model.addAttribute("blogPosts", blogPosts);
        return "blog_home";
    }

    @GetMapping("/search")
    public String searchBlogPosts(@RequestParam(name = "title", required = false) String title,
                                  @RequestParam(name = "publicationDate", required = false) String publicationDate,
                                  @RequestParam(name = "content", required = false) String content,
                                  Model model) {
        LocalDate date = null;
        if (publicationDate != null && !publicationDate.isEmpty()) {
            date = LocalDate.parse(publicationDate);
        }
        List<BlogPost> blogPosts = blogPostService.search(title, date, content);
        model.addAttribute("blogPosts", blogPosts);
        return "blog_home";
    }
}