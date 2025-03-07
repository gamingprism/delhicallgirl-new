package com.blogs.blogs.repo;

import com.blogs.blogs.entity.Blog;
import com.blogs.blogs.entity.Category;
import com.blogs.blogs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findByCategoryName(String category);
    List<Blog> findByAuthorOrderByCreatedAtDesc(User author);
    Optional<Blog> findBySlug(String slug);

    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);

    // Custom native query to find the latest 10 blogs ordered by createdAt descending
    @Query(value = "SELECT * FROM blog WHERE status = 'APPROVED' ORDER BY created_at DESC LIMIT 12", nativeQuery = true)
    List<Blog> findLatest12Blogs();

    Optional<Blog> findByPreviewToken(String previewToken);

    List<Blog> findByAuthorNotOrderByCreatedAtDesc(User author);


}
