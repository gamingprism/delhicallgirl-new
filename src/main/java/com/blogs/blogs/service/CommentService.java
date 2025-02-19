package com.blogs.blogs.service;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.response.CommentsResponse;

import java.util.List;

public interface CommentService {
    void addComment(Long blogId, String content, String email);

    List<CommentsResponse> getBlogComments(Blog blog);

    boolean editComment(Long commentId, Long blogId, String content, String userEmail);

    boolean deleteComment(Long commentId, Long blogId, String userEmail);
}
