package com.enterprise;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling // 启用定时任务
@EnableJpaRepositories("com.enterprise.repository")
@SpringBootApplication
public class JavaCommonFeaturesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaCommonFeaturesApplication.class, args);
    }

}
    