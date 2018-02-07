package com.jamespope101.phonebook;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by jpope on 07/02/2018.
 */
@EnableAutoConfiguration
@EnableTransactionManagement
@Configuration
@ComponentScan({"com.jamespope101.phonebook"})
public class ApplicationConfig {

}
