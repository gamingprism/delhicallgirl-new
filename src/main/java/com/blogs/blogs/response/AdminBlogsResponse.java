package com.blogs.blogs.response;

public class AdminBlogsResponse {
    private String title;
    private String author;
    private Long id;
    private String image;
    private String category;
    private String status;
    private String slug;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public AdminBlogsResponse(String title, String author, Long id, String image, String category, String status, String slug) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.image = image;
        this.category = category;
        this.status = status;
        this.slug=slug;
    }
}
