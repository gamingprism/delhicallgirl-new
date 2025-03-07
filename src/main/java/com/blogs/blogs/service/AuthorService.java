package com.blogs.blogs.service;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.entity.User;
import com.blogs.blogs.response.AuthorBlogsResponse;
import com.blogs.blogs.response.AuthorProfileResponse;
import com.blogs.blogs.response.BlogsPageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuthorService {
    List<AuthorBlogsResponse> getAuthorBlogs(User user);

    List<Category> getAllCategories();

    void createBlog(Blog blog, MultipartFile imageFile,String email);

    Blog getBlog(Long id);

    void editBlog(Long id, String title, String content, Long categoryId, MultipartFile imageFile, String imageAltText, String metaTitle, String metaDescription, String slug, String email);

    void deleteBlog(Long id);

    BlogsPageResponse getBlogByPreviewToken(String token);

    AuthorProfileResponse getAuthorProfile(User user);

    void updateAuthorProfile(String authorId, String name, MultipartFile picture);

    String getCurrentUserName();
}
