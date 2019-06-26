package com.smile;

import java.time.ZonedDateTime;
import java.util.function.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class K8sScUserServiceApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println(test(10));
        System.out.println(ZonedDateTime.now());
    }

    public Predicate test(int count) {
        return flag -> {
            if (count % 2 == 0) {
                return true;
            } else {
                return false;
            }
        };
    }


}
