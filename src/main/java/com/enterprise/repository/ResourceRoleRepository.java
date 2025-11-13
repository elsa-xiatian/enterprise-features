package com.enterprise.repository;



import com.enterprise.model.entity.Role;
import com.enterprise.model.entity.ResourceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRoleRepository extends JpaRepository<ResourceRole, Long> {
    // 替代 MyBatis 的 selectRoleNamesByResourceId：根据接口ID查询对应的角色名
    @Query("SELECT r.roleName FROM Role r " + // 使用实体类名Role，而非表名sys_role
            "JOIN ResourceRole rr ON r.id = rr.roleId " + // 使用实体类名ResourceRole，而非表名sys_resource_role
            "WHERE rr.resourceId = :resourceId")
    List<String> findRoleNamesByResourceId(@Param("resourceId") Long resourceId);
}
