package com.yz.mall.demo.spbt;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * @author yunze
 * @since 2025/11/2 23:38
 */
@Component
public class ThreadTest {

    @PostConstruct
    public void test() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000 * 10);
            System.out.println("thread " + i);
        }
    }
}
