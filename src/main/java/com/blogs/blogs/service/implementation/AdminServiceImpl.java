package com.blogs.blogs.service.implementation;

import com.blogs.blogs.config.RemoteStorage;
import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.entity.User;
import com.blogs.blogs.enums.BlogStatus;
import com.blogs.blogs.repo.BlogRepository;
import com.blogs.blogs.repo.CategoryRepository;
import com.blogs.blogs.repo.UserRepository;
import com.blogs.blogs.request.BlogRequest;
import com.blogs.blogs.response.AdminBlogsResponse;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.response.SingleBlogResponse;
import com.blogs.blogs.service.AdminService;
import com.blogs.blogs.service.AuthorService;
import com.blogs.blogs.utility.AppUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorService authorService;
    private final RemoteStorage remoteStorage;

    @Value("${admin.username}")
    private String adminUsername;

    public AdminServiceImpl(BlogRepository blogRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            AuthorService authorService, RemoteStorage remoteStorage) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.authorService = authorService;
        this.remoteStorage = remoteStorage;
    }

    @Override
    public Category createCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName.trim().toLowerCase());
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return new ArrayList<>(categoryRepository.findAll());
    }

    @Override
    public Category updateCategory(Long categoryId, String categoryName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("blogs category not found"));
        category.setName(categoryName.trim().toLowerCase());
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("blogs category not found"));
    }

    @Override
    @Transactional
    public Map<String, String> deleteCategory(Long categoryId) {
        Category blogsCategory = getCategory(categoryId);
        blogRepository.deleteAll(blogRepository.findByCategoryName(blogsCategory.getName()));
        categoryRepository.delete(blogsCategory);

        return Map.of("Status", "Success");
    }

    @Override
    public List<AdminBlogsResponse> getAllAdminBlogs() {
        User user = userRepository.findByEmail(adminUsername).orElseThrow(() -> new RuntimeException("User not found"));
        return blogRepository.findByAuthorOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminBlogsResponse> findAllUserBlogs() {
        User user = userRepository.findByEmail(adminUsername).orElseThrow(() -> new RuntimeException("User not found"));
        return blogRepository.findByAuthorNotOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AdminBlogsResponse convertToResponse(Blog blog) {
        return new AdminBlogsResponse(
                blog.getTitle(),
                blog.getAuthor().getName(),
                blog.getId(),
                remoteStorage.getCdnUrl(blog.getImage()),
                blog.getCategory().getName(),
                String.valueOf(blog.getStatus()),
                blog.getSlug()
        );
    }


    @Override
    public SingleBlogResponse getBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));

        SingleBlogResponse response = new SingleBlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setContent(blog.getContent());
        response.setImage(blog.getImage());
        response.setImageAltText(blog.getImageAltText());
        response.setMetaTitle(blog.getMetaTitle());
        response.setMetaDescription(blog.getMetaDescription());
        response.setSlug(blog.getSlug());
        response.setCategoryName(blog.getCategory().getName());
        response.setCategoryId(blog.getCategory().getId());
        if (!StringUtils.isEmpty(blog.getCustomButtonName()) && !StringUtils.isBlank(blog.getCustomButtonName())) {
            response.setCustomButtonName(blog.getCustomButtonName());
        }
        if (!StringUtils.isEmpty(blog.getCustomButtonUrl()) && !StringUtils.isBlank(blog.getCustomButtonUrl())) {
            response.setCustomButtonUrl(blog.getCustomButtonUrl());
        }

        return response;
    }

    @Override
    @Transactional
    public void createBlog(BlogRequest request) {
        userRepository.findByEmail(adminUsername).ifPresent(user -> {
            Blog blog = new Blog();
            blog.setTitle(request.getTitle());
            blog.setContent(request.getContent());

            if (!request.getImageFile().isEmpty()) {
                String imageKey = UUID.randomUUID() + ".jpg";
                remoteStorage.uploadFile(request.getImageFile(), imageKey);
                blog.setImage(imageKey);
            }
            blog.setImageAltText(request.getImageAltText());
            blog.setMetaTitle(request.getMetaTitle());
            blog.setMetaDescription(request.getMetaDescription());


            // Generate slug from title or provided slug
            String slug = "";
            if (request.getSlug() != null && !request.getSlug().isEmpty() && !request.getSlug().isBlank()){
                slug = request.getSlug();
            } else {
                slug = request.getTitle();
            }
            slug = slug.trim().toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-");
            if (blogRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("Slug already exists: " + slug);
            }
            blog.setSlug(slug);

            blog.setCategory(getCategory(request.getCategoryId()));
            blog.setAuthor(user);
            blog.setCreatedAt(LocalDateTime.now());
            blog.setViews(0L);
            blog.setPreviewToken(UUID.randomUUID().toString());
            blog.setCustomButtonName(request.getCustomButtonName());
            blog.setCustomButtonUrl(request.getCustomButtonUrl());
            blog.setStatus(BlogStatus.APPROVED);

            blogRepository.save(blog);
        });
    }

    @Override
    @Transactional
    public void editBlog(Long id, BlogRequest request) {
        blogRepository.findById(id).ifPresent(blog -> {
            if (isValid(request.getTitle())) {
                blog.setTitle(request.getTitle());
            }
            if (isValid(request.getContent())) {
                blog.setContent(request.getContent());
            }

            if (request.getImageFile() != null) {
                String imageKey = UUID.randomUUID() + ".jpg";
                remoteStorage.uploadFile(request.getImageFile(), imageKey);
                blog.setImage(imageKey);
            }

            if (isValid(request.getImageAltText())) {
                blog.setImageAltText(request.getImageAltText());
            }
            if (isValid(request.getMetaTitle())) {
                blog.setMetaTitle(request.getMetaTitle());
            }
            if (isValid(request.getMetaDescription())) {
                blog.setMetaDescription(request.getMetaDescription());
            }

            // Generate slug from title or provided slug
            String slug = isValid(request.getSlug()) ? request.getSlug() : request.getTitle();
            if (slug != null) {
                slug = slug.trim().toLowerCase()
                        .replaceAll("-", " ")
                        .replaceAll("[^a-z0-9\\s]", "")
                        .replaceAll("\\s+", "-");
                if (blogRepository.existsBySlugAndIdNot(slug, blog.getId())) {
                    throw new IllegalArgumentException("Slug already exists: " + slug);
                }
                blog.setSlug(slug);
            }

            if (request.getCategoryId() != null) {
                blog.setCategory(getCategory(request.getCategoryId()));
            }

            if (isValid(request.getCustomButtonName())) {
                blog.setCustomButtonName(request.getCustomButtonName());
            }
            if (isValid(request.getCustomButtonUrl())) {
                blog.setCustomButtonUrl(request.getCustomButtonUrl());
            }

            blogRepository.save(blog);
        });
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty() && !"null".equalsIgnoreCase(value.trim());
    }

    @Override
    public List<String> getBlogsStatus() {
        return Arrays.stream(BlogStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean changeBlogStatus(Long id, String status) {
        blogRepository.findById(id).ifPresent(blog -> {
            blog.setStatus(BlogStatus.valueOf(status));
            blogRepository.save(blog);
        });
        return true;
    }

    @Override
    public BlogsPageResponse getBlogData(Long id) {
        return blogRepository.findById(id).map(blog -> {
            BlogsPageResponse blogsResponse = new BlogsPageResponse();
            BeanUtils.copyProperties(blog, blogsResponse);
            blogsResponse.setImage(remoteStorage.getCdnUrl(blog.getImage()));
            blogsResponse.setCategory(blog.getCategory().getName());
            blogsResponse.setAuthor(blog.getAuthor().getName());
            blogsResponse.setId(blog.getId());
            blogsResponse.setMetaTitle(blog.getMetaTitle());
            blogsResponse.setMetaDescription(blog.getMetaDescription());
            blogsResponse.setDate(AppUtility.calculateTimeAgo(blog.getCreatedAt()));

            return blogsResponse;
        }).orElse(new BlogsPageResponse());
    }


}
