package com.blogs.blogs.service.implementation;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Comment;
import com.blogs.blogs.repo.BlogRepository;
import com.blogs.blogs.repo.CommentRepository;
import com.blogs.blogs.repo.UserRepository;
import com.blogs.blogs.response.CommentsResponse;
import com.blogs.blogs.service.CommentService;
import com.blogs.blogs.utility.AppUtility;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(BlogRepository blogRepository,
                              UserRepository userRepository,
                              CommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void addComment(Long blogId, String content, String email) {
        blogRepository.findById(blogId).ifPresent(blog -> {
            userRepository.findByEmail(email).ifPresent(user -> {
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setBlog(blog);
                comment.setCommenter(user);
                comment.setCreatedAt(LocalDateTime.now());

                commentRepository.save(comment);
            });
        });

    }

    @Override
    public List<CommentsResponse> getBlogComments(Blog blog) {
        List<Comment> comments = commentRepository.findByBlogOrderByCreatedAtDesc(blog);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.parallelStream().map(comment -> {
            CommentsResponse response = new CommentsResponse();
            response.setComment(comment.getContent());
            response.setAuthor(comment.getCommenter().getName());
            response.setDate(AppUtility.calculateTimeAgo(comment.getCreatedAt()));
            response.setAuthorLogo(comment.getCommenter().getPicture());
            response.setAuthorId(comment.getCommenter().getId());
            response.setId(comment.getId());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean editComment(Long commentId, Long blogId, String content, String userEmail) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getCommenter().getEmail().equals(userEmail) && comment.getBlog().getId().equals(blogId)) {
                comment.setContent(content);
                commentRepository.save(comment);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteComment(Long commentId, Long blogId, String userEmail) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getCommenter().getEmail().equals(userEmail) && comment.getBlog().getId().equals(blogId)) {
                commentRepository.delete(comment);
                return true;
            }
        }
        return false;
    }
}
