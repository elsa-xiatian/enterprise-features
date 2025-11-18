package com.enterprise.service;

import com.enterprise.model.entity.User;
import com.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AutoUnlockService {

    private final UserRepository userRepository;

    // 每30分钟执行一次（可调整频率）
    @Scheduled(cron = "0 0/30 * * * ?")
    public void autoUnlockExpiredAccounts() {
        // 1. 查询“已锁定+未禁用+锁定时间超过1小时”的账号
        //
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<User> lockedUsers = userRepository.findByLockedTrueAndDisabledFalseAndLockedTimeBefore(oneHourAgo);

        // 2. 自动解锁（设置locked=false，重置失败次数）
        for (User user : lockedUsers) {
            user.setLocked(false);
            user.setLoginFailedCount(0);
            userRepository.save(user);
            log.info("账号 {} 锁定超时，已自动解锁", user.getUsername());
        }
    }
}
