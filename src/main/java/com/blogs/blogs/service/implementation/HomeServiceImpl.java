package com.blogs.blogs.service.implementation;

import com.blogs.blogs.config.RemoteStorage;
import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.enums.BlogStatus;
import com.blogs.blogs.repo.BlogRepository;
import com.blogs.blogs.repo.CategoryRepository;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.response.BlogsResponse;
import com.blogs.blogs.service.HomeService;
import com.blogs.blogs.utility.AppUtility;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    private final CategoryRepository categoryRepo;
    private final BlogRepository blogRepo;
    private final RemoteStorage remoteStorage;

    @Value("${production.url}")
    private String prodUrl;

    public HomeServiceImpl(CategoryRepository categoryRepo,
                           BlogRepository blogRepo,
                           RemoteStorage remoteStorage) {
        this.categoryRepo = categoryRepo;
        this.blogRepo = blogRepo;
        this.remoteStorage = remoteStorage;
    }

    @Override
    public List<BlogsResponse> getBlogsForHome() {
        // Fetch the latest blogs
        List<Blog> blogs = blogRepo.findLatest12Blogs();
        List<BlogsResponse> blogsResponses = new ArrayList<>();

        // Loop through blogs and map to BlogsResponse
        blogs.stream().map(blog -> {
            BlogsResponse blogsResponse = new BlogsResponse();
            BeanUtils.copyProperties(blog, blogsResponse);
            blogsResponse.setImage(remoteStorage.getCdnUrl(blog.getImage()));
            blogsResponse.setCategory(blog.getCategory().getName());

            // Calculate how much time ago the blog was posted
            blogsResponse.setDate(AppUtility.calculateTimeAgo(blog.getCreatedAt()));
            return blogsResponse;
        }).forEach(blogsResponses::add); // Collect mapped objects into list

        return blogsResponses;
    }

    @Override
    public List<BlogsResponse> getCategoryBlogs(List<Blog> blogs) {
        if (blogs.isEmpty()) {
            return new ArrayList<>();
        }
        List<BlogsResponse> blogsResponses = new ArrayList<>();

        // Loop through blogs and map to BlogsResponse
        blogs.stream()
                .filter(blog -> blog.getStatus().equals(BlogStatus.APPROVED))
                .map(blog -> {
                    BlogsResponse blogsResponse = new BlogsResponse();
                    BeanUtils.copyProperties(blog, blogsResponse);
                    blogsResponse.setImage(remoteStorage.getCdnUrl(blog.getImage()));
                    blogsResponse.setCategory(blog.getCategory().getName());

                    // Calculate how much time ago the blog was posted
                    blogsResponse.setDate(AppUtility.calculateTimeAgo(blog.getCreatedAt()));
                    return blogsResponse;
                }).forEach(blogsResponses::add); // Collect mapped objects into list

        return blogsResponses;
    }

    @Override
    public BlogsPageResponse getBlogData(Blog blog) {

        BlogsPageResponse blogsResponse = new BlogsPageResponse();
        BeanUtils.copyProperties(blog, blogsResponse);
        blogsResponse.setImage(remoteStorage.getCdnUrl(blog.getImage()));
        blogsResponse.setCategory(blog.getCategory().getName());
        blogsResponse.setAuthor(blog.getAuthor().getName());
        blogsResponse.setId(blog.getId());
        blogsResponse.setMetaTitle(blog.getMetaTitle());
        blogsResponse.setMetaDescription(blog.getMetaDescription());
        blogsResponse.setAuthorImage(blog.getAuthor().getPicture());
        blogsResponse.setAuthorUrl(prodUrl + "profile/" + blog.getAuthor().getId());
        blogsResponse.setDatePublished(blog.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Calculate how much time ago the blog was posted
        blogsResponse.setDate(AppUtility.calculateTimeAgo(blog.getCreatedAt()));

        //Set Buttons
        if (blog.getCustomButtonName() != null && !blog.getCustomButtonName().isEmpty()){
            blogsResponse.setCustomButtonName(blog.getCustomButtonName());
        }
        if (blog.getCustomButtonUrl() != null && !blog.getCustomButtonUrl().isEmpty()){
            blogsResponse.setCustomButtonUrl(blog.getCustomButtonUrl());
        }

        //increase views
        blog.setViews(blog.getViews() + 1L);
        blogRepo.save(blog);
        return blogsResponse;
    }


}
