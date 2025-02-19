package com.blogs.blogs.controller;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.repo.BlogRepository;
import com.blogs.blogs.repo.CategoryRepository;
import com.blogs.blogs.repo.CommentRepository;
import com.blogs.blogs.service.CommentService;
import com.blogs.blogs.service.HomeService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final CategoryRepository categoryRepo;
    private final BlogRepository blogRepo;
    private final HomeService homeService;
    private final CommentService commentService;

    public HomeController(CategoryRepository categoryRepo,
                          BlogRepository blogRepo,
                          HomeService homeService,
                          CommentService commentService) {
        this.categoryRepo = categoryRepo;
        this.blogRepo = blogRepo;
        this.homeService = homeService;
        this.commentService = commentService;
    }


    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("homeBlogs", homeService.getBlogsForHome());
        String canonicalUrl = request.getRequestURL().toString();
        model.addAttribute("canonicalUrl", canonicalUrl);
        return "home";
    }

    @GetMapping("/sign-in")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/sign-out")
    public String logOut(Model model) {
        return "logout";
    }

    @GetMapping("/{identifier}")
    public String blogOrCategoryPage(HttpServletRequest request, @PathVariable("identifier") String identifier, Model model) {

        Optional<Blog> blogOptional = blogRepo.findBySlug(identifier);
        String canonicalUrl = request.getRequestURL().toString();

        if (blogOptional.isPresent()) {
            model.addAttribute("blog", homeService.getBlogData(blogOptional.get()));
            model.addAttribute("categories", categoryRepo.findAll());
            model.addAttribute("recentBlogs", homeService.getBlogsForHome());
            model.addAttribute("comments", commentService.getBlogComments(blogOptional.get()));
            model.addAttribute("canonicalUrl", canonicalUrl);
            return "blog"; // Return blog page if slug is found
        }

        String categoryName = identifier.trim().replaceAll("-", " ");
        Optional<Category> category = categoryRepo.findByNameEqualsIgnoreCase(categoryName);
        if (category.isPresent()) {
            List<Blog> blogs = blogRepo.findByCategoryName(categoryName);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("categoryBlogs", homeService.getCategoryBlogs(blogs));
            model.addAttribute("categories", categoryRepo.findAll());
            model.addAttribute("recentBlogs", homeService.getBlogsForHome());
            model.addAttribute("canonicalUrl", canonicalUrl);
            return "category"; // Return category page if slug is not found
        }
        return "redirect:/";
    }

}
