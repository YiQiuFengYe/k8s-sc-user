package com.smile;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.smile"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class K8sScUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sScUserServiceApplication.class, args);
    }

}


/*

-Djava.security.egd=file:/dev/./urandom
\
--spring.profile.active=master
\
--diy.eureka.server.port=8761
\
--diy.eureka.user=smile
\
--diy.eureka.password=smilelxy
\
--diy.eureka.host.master=127.0.0.1
\
--diy.eureka.host.backup01=127.0.0.1
\
--diy.eureka.host.backup02=127.0.0.1
\


 */

