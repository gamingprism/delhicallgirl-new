package com.blogs.blogs.service.implementation;

import com.blogs.blogs.config.RemoteStorage;
import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.entity.User;
import com.blogs.blogs.enums.BlogStatus;
import com.blogs.blogs.repo.BlogRepository;
import com.blogs.blogs.repo.CategoryRepository;
import com.blogs.blogs.repo.UserRepository;
import com.blogs.blogs.response.AuthorBlogsResponse;
import com.blogs.blogs.response.AuthorProfileResponse;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.service.AuthorService;
import com.blogs.blogs.service.HomeService;
import com.blogs.blogs.utility.AppUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RemoteStorage remoteStorage;
    private final HomeService homeService;

    public AuthorServiceImpl(BlogRepository blogRepository,
                             CategoryRepository categoryRepository,
                             UserRepository userRepository,
                             RemoteStorage remoteStorage,
                             HomeService homeService) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.remoteStorage = remoteStorage;
        this.homeService = homeService;
    }

    @Override
    public List<AuthorBlogsResponse> getAuthorBlogs(User user) {
        List<AuthorBlogsResponse> responses = new ArrayList<>();
        blogRepository.findByAuthorOrderByCreatedAtDesc(user).forEach(blog -> {
            AuthorBlogsResponse response = new AuthorBlogsResponse();
            BeanUtils.copyProperties(blog, response);
            response.setImage(remoteStorage.getCdnUrl(blog.getImage()));
            response.setCategory(blog.getCategory().getName());
            response.setDate(AppUtility.calculateTimeAgo(blog.getCreatedAt()));
            response.setPreviewToken(blog.getPreviewToken());
            response.setStatus(String.valueOf(blog.getStatus()));
            responses.add(response);
        });
        return responses;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public void createBlog(Blog blog, MultipartFile imageFile, String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            blog.setAuthor(user);
            blog.setCreatedAt(LocalDateTime.now());

            //validate slug
            String slug = blog.getSlug() == null ? blog.getTitle().trim().toLowerCase().replaceAll(" ", "-") :
                    blog.getSlug().trim().toLowerCase().replaceAll(" ", "-");

            if (blogRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("Slug already exists: " + slug);
            }

            blog.setSlug(slug);
            blog.setViews(0L);

            AppUtility.validateBlogContent(blog.getContent());

            if (!imageFile.isEmpty()) {
                String imageKey = UUID.randomUUID() + ".jpg";
                remoteStorage.uploadFile(imageFile, imageKey);
                blog.setImage(imageKey);
            }
            blog.setPreviewToken(UUID.randomUUID().toString());
            blog.setStatus(BlogStatus.PENDING);
            blogRepository.save(blog);
        });
    }

    @Override
    public Blog getBlog(Long id){
        return blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    @Override
    public void editBlog(Long id, String title,
                         String content, Long categoryId,
                         MultipartFile imageFile, String imageAltText,
                         String metaTitle, String metaDescription,
                         String slug, String email) {

        log.info(String.valueOf(imageFile));

        userRepository.findByEmail(email).flatMap(user -> blogRepository.findById(id)).ifPresent(blog -> { // Retrieve the existing blog

            blog.setTitle(title);
            blog.setContent(content);

            Category category = new Category();
            category.setId(categoryId);
            blog.setCategory(category);
            blog.setImageAltText(imageAltText);
            blog.setMetaTitle(metaTitle);
            blog.setMetaDescription(metaDescription);

            // validate slug
            String validatedSlug = title.trim().toLowerCase().replaceAll(" ", "-");
            if (blogRepository.existsBySlugAndIdNot(validatedSlug, blog.getId())) {
                throw new IllegalArgumentException("Slug already exists: " + validatedSlug);
            }
            blog.setSlug(validatedSlug);
            AppUtility.validateBlogContent(blog.getContent());

            if (!imageFile.isEmpty()) {
                String imageKey = UUID.randomUUID() + ".jpg";
                remoteStorage.uploadFile(imageFile, imageKey);
                blog.setImage(imageKey);
            }

            blogRepository.save(blog); // Save the *retrieved and updated* blog
        });
    }

    @Override
    public void deleteBlog(Long id){
        blogRepository.findById(id).ifPresent(blog -> {
            remoteStorage.deleteFile(blog.getImage());
            blogRepository.delete(blog);
        });
    }

    @Override
    public BlogsPageResponse getBlogByPreviewToken(String token) {
        Blog blog = blogRepository.findByPreviewToken(token).orElse(new Blog());
        return homeService.getBlogData(blog);
    }

    @Override
    public AuthorProfileResponse getAuthorProfile(User user) {
        AuthorProfileResponse response = new AuthorProfileResponse();
        response.setAuthorId(user.getId());
        response.setName(user.getName());
        response.setPicture(user.getPicture());
        response.setBlogs(getAuthorBlogs(user));
        return response;
    }

    @Override
    public void updateAuthorProfile(String authorId, String name, MultipartFile picture) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);

        if (picture != null && !picture.isEmpty()) {
            String imageKey = UUID.randomUUID() + ".jpg";
            remoteStorage.uploadFile(picture, imageKey);
            user.setPicture(remoteStorage.getCdnUrl(imageKey));
        }

        userRepository.save(user);
    }

    @Override
    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get the email (or name) from the principal
        User user = userRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getName(); // Return the user's name
    }

}
