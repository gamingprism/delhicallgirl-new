package com.blogs.blogs.service;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.response.BlogsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface HomeService {
    List<BlogsResponse> getBlogsForHome();

    List<BlogsResponse> getCategoryBlogs(List<Blog> blogs);

    BlogsPageResponse getBlogData(Blog blog);
}
