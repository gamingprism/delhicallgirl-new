package com.blogs.blogs.controller;

import com.blogs.blogs.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/blog/{blogId}/comment")
public class UserController {

    private final CommentService commentService;

    public UserController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addComment(@PathVariable Long blogId,
                                        @RequestParam String content,
                                        @AuthenticationPrincipal OAuth2User principal) {
        commentService.addComment(blogId, content, principal.getAttribute("email"));
        return new ResponseEntity<>(Map.of("message", "Comment added successfully"), HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editComment(@PathVariable Long blogId,
                                         @PathVariable Long commentId,
                                         @RequestParam String content,
                                         @AuthenticationPrincipal OAuth2User principal) {
        boolean isUpdated = commentService.editComment(commentId, blogId, content, principal.getAttribute("email"));
        return isUpdated ? ResponseEntity.ok(Map.of("message", "Comment updated successfully"))
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized to edit this comment"));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(@PathVariable Long blogId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal OAuth2User principal) {
        boolean isDeleted = commentService.deleteComment(commentId, blogId, principal.getAttribute("email"));
        return isDeleted ? ResponseEntity.ok(Map.of("message", "Comment deleted successfully"))
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized to delete this comment"));
    }
}
