package com.blogs.blogs.controller;

import com.blogs.blogs.config.RemoteStorage;
import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.User;
import com.blogs.blogs.repo.UserRepository;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);
    private final UserRepository userRepository;
    private final RemoteStorage remoteStorage;
    private final AuthorService authorService;

    public AuthorController(UserRepository userRepository,
                            RemoteStorage remoteStorage,
                            AuthorService authorService) {
        this.userRepository = userRepository;
        this.remoteStorage = remoteStorage;
        this.authorService = authorService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-blogs")
    public String authorPage(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if(principal == null){
            return "redirect:/sign-in";
        }
        String email = principal.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        userOptional.ifPresent(user -> model.addAttribute("blogs", authorService.getAuthorBlogs(user)));
        return "author";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write-blog")
    public String writeBlogForm(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if(principal == null) {
            return "redirect:/sign-in";
        }

        model.addAttribute("blog", new Blog());
        model.addAttribute("categories", authorService.getAllCategories());
        return "write-blog";
    }

    @PostMapping("/save-blog")
    public String saveBlog(@ModelAttribute("blog") Blog blog,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           @AuthenticationPrincipal OAuth2User principal,
                           Model model) {
        if (principal == null) {
            return "redirect:/sign-in";
        }
        try {
            authorService.createBlog(blog, imageFile, principal.getAttribute("email"));
            return "redirect:/my-blogs";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
        }

        // Preserve form data
        model.addAttribute("categories", authorService.getAllCategories());
        return "write-blog";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit-blog/{id}")
    public String editBlogForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "redirect:/sign-in";
        }

        model.addAttribute("blog", authorService.getBlog(id));
        model.addAttribute("categories", authorService.getAllCategories());
        return "edit-blog";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update-blog")
    public String updateBlog(@RequestParam("id") Long id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam("imageAltText") String imageAltText,
                             @RequestParam("metaTitle") String metaTitle,
                             @RequestParam("metaDescription") String metaDescription,
                             @RequestParam(value = "slug", required = false) String slug,
                             @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            return "redirect:/sign-in";
        }

        try {
            authorService.editBlog(id, title, content, categoryId, imageFile, imageAltText,
                    metaTitle, metaDescription, slug, principal.getAttribute("email"));
            return "redirect:/my-blogs";
        } catch (Exception e) {
          e.printStackTrace();
        }

        return "redirect:/my-blogs";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete-blog/{id}")
    public String deleteBlog(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/sign-in";
        }
        authorService.deleteBlog(id);
        return "redirect:/my-blogs";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/preview/{previewToken}")
    public String previewBlog(@PathVariable String previewToken,
                              @AuthenticationPrincipal OAuth2User principal,
                              Model model) {
        if (principal == null) {
            return "redirect:/sign-in";
        }

        String email = principal.getAttribute("name");
        BlogsPageResponse blog = authorService.getBlogByPreviewToken(previewToken);

        if (blog == null || !blog.getAuthor().equals(email)) {
            return "no-permission";
        }

        model.addAttribute("blog", blog);
        return "preview-blog";
    }
}


