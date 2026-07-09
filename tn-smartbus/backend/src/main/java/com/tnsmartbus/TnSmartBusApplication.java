package com.tnsmartbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Redis repository auto-configuration is excluded deliberately: this project
 * only uses Redis as a cache/session store, never through Spring Data Redis
 * repository interfaces. With both spring-data-jpa and spring-data-redis on
 * the classpath, Spring Data's "strict repository configuration mode" can't
 * tell which module should back custom marker-interface repositories (e.g.
 * RouteSearchRepository, which extends the base Repository<T,ID> interface
 * for a native @Query-only projection) and silently skips creating a bean
 * for them. Excluding Redis repository scanning removes the ambiguity.
 */
@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
@EnableScheduling
public class TnSmartBusApplication {
    public static void main(String[] args) {
        SpringApplication.run(TnSmartBusApplication.class, args);
    }
}
