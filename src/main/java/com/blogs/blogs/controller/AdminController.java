package com.blogs.blogs.controller;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.request.BlogRequest;
import com.blogs.blogs.response.AdminBlogsResponse;
import com.blogs.blogs.response.BlogsPageResponse;
import com.blogs.blogs.response.SingleBlogResponse;
import com.blogs.blogs.service.AdminService;
import com.blogs.blogs.service.AuthorService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;
    private final AuthorService authorService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminController(AdminService adminService,
                           AuthorService authorService) {
        this.adminService = adminService;
        this.authorService = authorService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateAdmin(@RequestParam String username, @RequestParam String password) {
        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        }
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/category/create", method = RequestMethod.POST)
    public ResponseEntity<Category> createBlogsCategory(@RequestBody String categoryName) {
        Category createdCategory = adminService.createCategory(categoryName.replaceAll("\"", ""));
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/category/get-all", method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getAllBlogsCategories() {
        List<Category> categories = adminService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @RequestMapping(path = "/category/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Category> updateBlogsCategory(@PathVariable("id") Long blogCategoryId,
                                                        @RequestBody String categoryName) {
        Category updatedCategory = adminService.updateCategory(blogCategoryId, categoryName.replaceAll("\"", ""));
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @RequestMapping(path = "/category/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<Category> getBlogCategory(@PathVariable("id") Long blogCategoryId) {
        Category category = adminService.getCategory(blogCategoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(path = "/category/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> deleteBlogCategory(@PathVariable("id") Long blogCategoryId) {
        Map<String, String> response = adminService.deleteCategory(blogCategoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/blogs/create-blog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createBlog(@ModelAttribute BlogRequest blogRequest) {
        adminService.createBlog(blogRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Blog created successfully");
    }

    @RequestMapping(path = "/blogs/get-blog/{id}", method = RequestMethod.GET)
    public ResponseEntity<SingleBlogResponse> getBlog(@PathVariable("id") Long id) {
        SingleBlogResponse blog = adminService.getBlog(id);
        return new ResponseEntity<>(blog, HttpStatus.OK);

    }

    @RequestMapping(path = "/blogs/edit-blog/{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editBlog(@PathVariable("id") Long id,
                                           @ModelAttribute BlogRequest blogRequest) {
        adminService.editBlog(id, blogRequest);
        return ResponseEntity.ok("Blog updated successfully");
    }

    @RequestMapping(value = "/blogs/delete-blog/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBlog(@PathVariable("id") Long id) {
        authorService.deleteBlog(id);
        return ResponseEntity.ok("Blog deleted successfully");
    }

    @RequestMapping(value = "/blogs/get-all", method = RequestMethod.GET)
    public ResponseEntity<List<AdminBlogsResponse>> getAllAdminBlogs() {
        List<AdminBlogsResponse> allBlogs = adminService.getAllAdminBlogs();
        return new ResponseEntity<>(allBlogs, HttpStatus.OK);
    }

    @RequestMapping(value = "/blogs/get-all-author-blogs", method = RequestMethod.GET)
    public ResponseEntity<List<AdminBlogsResponse>> findAllUserBlogs() {
        List<AdminBlogsResponse> allBlogs = adminService.findAllUserBlogs();
        return new ResponseEntity<>(allBlogs, HttpStatus.OK);
    }

    @RequestMapping(value = "/blogs/get-all-status", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAllBlogStatuses() {
        return new ResponseEntity<>(adminService.getBlogsStatus(), HttpStatus.OK);
    }

    @RequestMapping(value = "/blogs/change-status/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> changeBlogStatus(@PathVariable("id") Long id,
                                              @RequestParam String status) {
        return new ResponseEntity<>(adminService.changeBlogStatus(id, status), HttpStatus.OK);
    }

    @RequestMapping(value = "/blogs/get-blog-page/{id}", method = RequestMethod.GET)
    public ResponseEntity<BlogsPageResponse> getBlogData(@PathVariable("id") Long id) {
        return new ResponseEntity<>(adminService.getBlogData(id), HttpStatus.OK);
    }
}
