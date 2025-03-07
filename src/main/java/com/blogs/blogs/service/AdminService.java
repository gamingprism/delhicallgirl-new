package com.blogs.blogs.service;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.request.BlogRequest;
import com.blogs.blogs.response.AdminBlogsResponse;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.response.SingleBlogResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Category createCategory(String categoryName);

    List<Category> getAllCategories();

    Category updateCategory(Long categoryId, String categoryName);

    Category getCategory(Long categoryId);

    Map<String, String> deleteCategory(Long categoryId);

    List<AdminBlogsResponse> getAllAdminBlogs();

    List<AdminBlogsResponse> findAllUserBlogs();

    SingleBlogResponse getBlog(Long id);

    void createBlog(BlogRequest request);

    void editBlog(Long id, BlogRequest request);

    List<String> getBlogsStatus();

    boolean changeBlogStatus(Long id, String status);

    BlogsPageResponse getBlogData(Long id);
}
