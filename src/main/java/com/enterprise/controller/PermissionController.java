package com.enterprise.controller;

import com.enterprise.model.dto.ResourceAddDTO;
import com.enterprise.model.dto.ResourceRoleBindDTO;
import com.enterprise.model.dto.ResourceRoleDeleteDTO;
import com.enterprise.model.entity.Resource;
import com.enterprise.model.entity.ResourceRole;
import com.enterprise.model.vo.ResourceRoleVO;
import com.enterprise.repository.ResourceRepository;
import com.enterprise.repository.ResourceRoleRepository;
import com.enterprise.service.ResourceRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permission")
@RequiredArgsConstructor
@Tag(name = "权限管理接口", description = "仅管理员可操作，用于动态管理接口权限规则")
@PreAuthorize("hasRole('ADMIN')") // 整个控制器的接口都需要ADMIN角色
public class PermissionController {

    private final ResourceRepository resourceRepository;
    private final ResourceRoleRepository resourceRoleRepository;
    private final ResourceRoleService resourceRoleService;

    // 1. 新增接口资源
    @PostMapping("/resource/add")
    @Operation(summary = "新增接口资源", description = "往sys_resource表添加新接口，用于后续绑定角色")
    public ResponseEntity<String> addResource(@Valid @RequestBody ResourceAddDTO dto) {

        // 转换DTO为实体类
        Resource resource = new Resource();
        resource.setResourceUrl(dto.getResourceUrl());
        resource.setHttpMethod(dto.getHttpMethod().toUpperCase()); // 统一转为大写（如get→GET）
        resource.setResourceName(dto.getResourceName());

        // 保存到数据库
        resourceRepository.save(resource);
        return ResponseEntity.ok("接口资源新增成功，ID：" + resource.getId());
    }

    // 2. 绑定角色到接口（给接口分配可访问的角色）
    @PostMapping("/resource/bind-role")
    @Operation(summary = "绑定角色到接口", description = "往sys_resource_role表添加关联关系，配置接口允许的角色")
    public ResponseEntity<String> bindRoleToResource(@Valid @RequestBody ResourceRoleBindDTO dto) {
        // 检查接口和角色是否存在（简化处理，实际项目可加校验）
        // 转换DTO为实体类
        ResourceRole resourceRole = new ResourceRole();
        resourceRole.setResourceId(dto.getResourceId());
        resourceRole.setRoleId(dto.getRoleId());

        // 保存关联关系
        resourceRoleRepository.save(resourceRole);
        return ResponseEntity.ok("接口与角色绑定成功");
    }

    // 3. 刷新权限规则（让新配置生效）
    @PostMapping("/refresh")
    @Operation(summary = "刷新权限规则", description = "重新加载数据库中的权限规则到内存，新配置立即生效")
    public ResponseEntity<String> refreshPermissionRules() {
        resourceRoleService.refreshResourceRoleRules();
        return ResponseEntity.ok("权限规则刷新成功，当前加载 " + resourceRoleService.getResourceRoleMapSize() + " 个接口规则");
    }

    @GetMapping("/resource/list")
    @Operation(summary = "查询所有接口权限规则", description = "返回所有接口及其绑定的角色列表")
    public ResponseEntity<List<ResourceRoleVO>> listAllResourceRoles() {
        return ResponseEntity.ok(resourceRoleService.getAllResourceRoleRules());
    }

    @DeleteMapping("/resource/unbind-role")
    @Operation(summary = "解除接口与角色的绑定", description = "删除sys_resource_role表中的关联记录")
    public ResponseEntity<String> unbindRoleFromResource(@Valid @RequestBody ResourceRoleDeleteDTO dto) {
        if (resourceRoleRepository.existsById(dto.getId())) {
            resourceRoleRepository.deleteById(dto.getId());
            resourceRoleService.refreshResourceRoleRules(); // 自动刷新
            return ResponseEntity.ok("接口与角色的绑定已解除，规则已自动刷新");
        } else {
            return ResponseEntity.badRequest().body("关联记录不存在");
        }
    }
}
