package com.enterprise.service;

import com.enterprise.model.dto.UserInfoResponse;
import com.enterprise.model.dto.UserUpdateRequest;

public interface UserService {

    UserInfoResponse getUserInfoByUsername(String username);

    void updateUserInfo(UserUpdateRequest request);
}
