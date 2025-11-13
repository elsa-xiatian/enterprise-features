package com.enterprise.repository;

// com.enterprise.repository.ResourceRepository.java
import com.enterprise.model.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // 替代 MyBatis 的 selectByUrlAndMethod：根据路径和方法查询接口
    @Query("SELECT r FROM Resource r WHERE r.resourceUrl = :url AND r.httpMethod = :method")
    Resource findByResourceUrlAndHttpMethod(@Param("url") String url, @Param("method") String method);

    // 替代 MyBatis 的 selectAll：查询所有接口资源（Service 层加载规则用）
    @Override
    List<Resource> findAll();
}
