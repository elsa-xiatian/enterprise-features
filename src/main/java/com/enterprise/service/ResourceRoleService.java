package com.enterprise.service;

import com.enterprise.model.entity.Resource;
import com.enterprise.model.vo.ResourceRoleVO;
import com.enterprise.repository.ResourceRepository;
import com.enterprise.repository.ResourceRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceRoleService {
    // 注入 JPA Repository
    private final ResourceRepository resourceRepository;
    private final ResourceRoleRepository resourceRoleRepository;

    // 存储接口-角色规则
    private final Map<String, List<String>> resourceRoleMap = new ConcurrentHashMap<>();

    // 系统启动时加载规则（逻辑不变，仅替换查询方式）
    @PostConstruct
    public void loadResourceRoleRules() {

        // 1. 查询所有接口资源（用 JPA 的 findAll()）
        List<Resource> resourceList = resourceRepository.findAll();
        // 2. 遍历接口，查询每个接口对应的角色（用 JPA 的自定义方法）
        for (Resource resource : resourceList) {
            String key = buildResourceKey(resource.getResourceUrl(), resource.getHttpMethod());
            List<String> roleNames = resourceRoleRepository.findRoleNamesByResourceId(resource.getId());
            resourceRoleMap.put(key, roleNames);
        }
        log.info("动态权限规则加载完成，共加载 {} 个接口规则", resourceRoleMap.size());
    }

    // 构建 Key 的方法（不变）
    private String buildResourceKey(String url, String method) {
        return url + ":" + method.toUpperCase();
    }

    // 对外提供方法（不变）
    public List<String> getRoleNamesByResource(String url, String method) {
        String key = buildResourceKey(url, method);
        return resourceRoleMap.getOrDefault(key, Collections.emptyList());
    }

    // 刷新规则方法（不变）
    public void refreshResourceRoleRules() {
        resourceRoleMap.clear();
        loadResourceRoleRules();
        log.info("动态权限规则已刷新");
    }

    public int getResourceRoleMapSize() {
        return resourceRoleMap.size();
    }

    public List<ResourceRoleVO> getAllResourceRoleRules() {
        List<ResourceRoleVO> res = new ArrayList<>();
        List<Resource> resources = resourceRepository.findAll();
        for (Resource resource : resources) {
            ResourceRoleVO vo = new ResourceRoleVO();
            vo.setResourceId(resource.getId());
            vo.setResourceUrl(resource.getResourceUrl());
            vo.setHttpMethod(resource.getHttpMethod());
            vo.setResourceName(resource.getResourceName());

            // 调用已有的方法查询角色名列表
            List<String> roleNames = resourceRoleRepository.findRoleNamesByResourceId(resource.getId());
            vo.setRoleNames(roleNames);

            res.add(vo);
        }
        return res;
    }
}
