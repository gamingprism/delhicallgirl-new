package com.blogs.blogs.response;

import com.blogs.blogs.entity.Blog;

import java.util.List;

public class AuthorProfileResponse {
    private String authorId;
    private String name;
    private String picture;
    private List<AuthorBlogsResponse> blogs;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<AuthorBlogsResponse> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<AuthorBlogsResponse> blogs) {
        this.blogs = blogs;
    }
}
