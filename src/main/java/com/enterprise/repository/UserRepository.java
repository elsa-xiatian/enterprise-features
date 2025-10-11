package com.enterprise.repository;

import com.enterprise.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //基于JPA简化数据访问层

    // 根据用户名查询用户
    Optional<User> findByUsername(String username);

    // 检查用户名是否已存在
    boolean existsByUsername(String username);
}
