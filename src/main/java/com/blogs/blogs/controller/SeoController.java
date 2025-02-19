package com.blogs.blogs.controller;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.repo.BlogRepository;
import com.blogs.blogs.repo.CategoryRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin("*")
public class SeoController {
    private final CategoryRepository categoryRepository;
    private final BlogRepository blogRepository;

    public SeoController(CategoryRepository categoryRepository, BlogRepository blogRepository) {
        this.categoryRepository = categoryRepository;
        this.blogRepository = blogRepository;
    }


    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String generateSitemap(HttpServletResponse response) {

        List<String> categories = categoryRepository.findAll().stream().map(Category::getName).toList();
        List<String> blogs = blogRepository.findAll().stream().map(Blog::getSlug).toList();

        List<String> urls = new ArrayList<>();
        urls.addAll(categories);
        urls.addAll(blogs);


        String baseUrl = "https://delhicallgirl.agency";
        String today = LocalDate.now().toString();

        StringBuilder sitemap = new StringBuilder();
        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Add base domain entry
        sitemap.append("  <url>\n");
        sitemap.append("    <loc>").append(baseUrl).append("/</loc>\n");
        sitemap.append("    <lastmod>").append(today).append("</lastmod>\n");
        sitemap.append("    <changefreq>daily</changefreq>\n");
        sitemap.append("    <priority>1.0</priority>\n");
        sitemap.append("  </url>\n");

        // Add individual location entries
        for (String url : urls) {
            String locationUrl = baseUrl + "/" + url.toLowerCase().replace(" ", "-");
            sitemap.append("  <url>\n");
            sitemap.append("    <loc>").append(locationUrl).append("</loc>\n");
            sitemap.append("    <lastmod>").append(today).append("</lastmod>\n");
            sitemap.append("    <changefreq>daily</changefreq>\n");
            sitemap.append("    <priority>1.0</priority>\n");
            sitemap.append("  </url>\n");
        }

        sitemap.append("</urlset>");
        return sitemap.toString();
    }

    @GetMapping("/robots.txt")
    public ResponseEntity<Resource> getRobotsTxt() throws IOException {
        Resource robotsTxt = new ClassPathResource("static/robots.txt");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                .body(robotsTxt);
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Resource> getFavicon() throws IOException {
        return getResourceResponse("static/favicon.ico", "image/x-icon");
    }

    @GetMapping("/favicon-32x32.png")
    public ResponseEntity<Resource> getFavicon32() throws IOException {
        return getResourceResponse("static/favicon-32x32.png", "image/png");
    }

    @GetMapping("/favicon-16x16.png")
    public ResponseEntity<Resource> getFavicon16() throws IOException {
        return getResourceResponse("static/favicon-16x16.png", "image/png");
    }

    @GetMapping("/apple-icon-57x57.png")
    public ResponseEntity<Resource> getAppleIcon57() throws IOException {
        return getResourceResponse("static/apple-icon-57x57.png", "image/png");
    }

    @GetMapping("/apple-icon-120x120.png")
    public ResponseEntity<Resource> getAppleIcon120() throws IOException {
        return getResourceResponse("static/apple-icon-120x120.png", "image/png");
    }

    @GetMapping("/android-icon-36x36.png")
    public ResponseEntity<Resource> getAndroidIcon36() throws IOException {
        return getResourceResponse("static/android-icon-36x36.png", "image/png");
    }

    @GetMapping("/android-icon-48x48.png")
    public ResponseEntity<Resource> getAndroidIcon48() throws IOException {
        return getResourceResponse("static/android-icon-48x48.png", "image/png");
    }

    @GetMapping("/android-icon-144x144.png")
    public ResponseEntity<Resource> getAndroidIcon144() throws IOException {
        return getResourceResponse("static/android-icon-144x144.png", "image/png");
    }

    @GetMapping("/android-icon-192x192.png")
    public ResponseEntity<Resource> getAndroidIcon192() throws IOException {
        return getResourceResponse("static/android-icon-192x192.png", "image/png");
    }

    @GetMapping("/manifest.json")
    public ResponseEntity<Resource> getManifest() throws IOException {
        return getResourceResponse("static/manifest.json", "application/json");
    }

    private ResponseEntity<Resource> getResourceResponse(String path, String contentType) throws IOException {
        Resource resource = new ClassPathResource(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }

}
