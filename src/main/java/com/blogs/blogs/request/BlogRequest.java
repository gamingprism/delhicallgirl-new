package com.blogs.blogs.request;

import org.springframework.web.multipart.MultipartFile;

public class BlogRequest {
    private String title;
    private String content;
    private Long categoryId;
    private MultipartFile imageFile;
    private String imageAltText;
    private String metaTitle;
    private String metaDescription;
    private String slug;
    private String email;
    private String customButtonName;
    private String customButtonUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageAltText() {
        return imageAltText;
    }

    public void setImageAltText(String imageAltText) {
        this.imageAltText = imageAltText;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomButtonName() {
        return customButtonName;
    }

    public void setCustomButtonName(String customButtonName) {
        this.customButtonName = customButtonName;
    }

    public String getCustomButtonUrl() {
        return customButtonUrl;
    }

    public void setCustomButtonUrl(String customButtonUrl) {
        this.customButtonUrl = customButtonUrl;
    }

    @Override
    public String toString() {
        return "BlogRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", categoryId=" + categoryId +
                ", imageFile=" + imageFile +
                ", imageAltText='" + imageAltText + '\'' +
                ", metaTitle='" + metaTitle + '\'' +
                ", metaDescription='" + metaDescription + '\'' +
                ", slug='" + slug + '\'' +
                ", email='" + email + '\'' +
                ", customButtonName='" + customButtonName + '\'' +
                ", customButtonUrl='" + customButtonUrl + '\'' +
                '}';
    }
}
