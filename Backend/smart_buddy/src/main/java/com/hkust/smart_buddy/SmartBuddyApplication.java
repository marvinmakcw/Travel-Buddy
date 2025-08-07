package com.hkust.smart_buddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SmartBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartBuddyApplication.class, args);
    }

}
