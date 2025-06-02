/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.blazartech.springexpandjsonvariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author aar1069
 */
@SpringBootApplication
public class SpringExpandJsonVariable {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    public static void main(String[] args) {
        SpringApplication.run(SpringExpandJsonVariable.class, args);
    }
}
