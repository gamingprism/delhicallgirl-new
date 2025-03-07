package com.blogs.blogs.response;

public class SingleBlogResponse {

    private Long id;
    private String title;
    private String content;
    private String image;
    private String imageAltText;
    private String metaTitle;
    private String metaDescription;
    private String slug;
    private String categoryName;
    private Long categoryId;
    private String customButtonName;
    private String customButtonUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
}
