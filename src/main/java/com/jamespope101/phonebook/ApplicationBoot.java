package com.jamespope101.phonebook;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by jpope on 07/02/2018.
 */
@ComponentScan
@EnableAutoConfiguration
public class ApplicationBoot {

    private final ConfigurableApplicationContext ctx;

    public ApplicationBoot(String... args) {
        ctx = new SpringApplicationBuilder(ApplicationConfig.class)
            .bannerMode(Mode.CONSOLE)
            .addCommandLineProperties(true)
            .properties(ImmutableMap.of("spring.config.name", "phonebook-api"))
            .run(args);
    }

    public static void main(String[] args) {
        new ApplicationBoot(args);
    }

    public ApplicationContext context() {
        return ctx;
    }
}
