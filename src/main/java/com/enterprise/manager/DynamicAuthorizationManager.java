package com.enterprise.manager;

import com.enterprise.service.ResourceRoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final ResourceRoleService resourceRoleService;

    // 核心校验逻辑（对应旧版的decide方法）
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        // 1. 获取当前请求的接口路径和请求方法
        HttpServletRequest request = context.getRequest();
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();

        // 2. 从内存中获取该接口对应的角色列表
        List<String> requiredRoles = resourceRoleService.getRoleNamesByResource(requestUrl, requestMethod);

        // 3. 获取当前登录用户的认证信息
        Authentication authentication = authenticationSupplier.get();

        // 4. 权限校验逻辑
        if (requiredRoles.isEmpty()) {
            // 接口未配置角色规则：默认“登录即可访问”
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            return new AuthorizationDecision(isAuthenticated);
        }

        // 5. 检查用户是否拥有所需角色
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false); // 未登录，无权限
        }

        Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();
        boolean hasPermission = userAuthorities.stream()
                .anyMatch(authority -> requiredRoles.contains(authority.getAuthority()));

        return new AuthorizationDecision(hasPermission); // 有则返回true，无则false
    }
}
