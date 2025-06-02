/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.blazartech.springexpandjsonvariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author aar1069
 */
@ExtendWith(SpringExtension.class)
@Slf4j
public class ExpandVariableCommandLineRunnerTest {
    
    @TestConfiguration
    @PropertySource("classpath:unittest.properties")
    public static class ExpandVariableCommandLineRunnerTestConfiguration {
        
        @Bean
        public ExpandVariableCommandLineRunner instance() {
            return new ExpandVariableCommandLineRunner();
        }
        
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
    
    @Autowired
    private ExpandVariableCommandLineRunner instance;
    
    @Value("${unittest.sample.data.file}")
    private Resource testJsonResource;
    
    @Value("${unittest.sample.expected.file}")
    private Resource expectedJsonResource;
    
    public ExpandVariableCommandLineRunnerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    private String readJson(Resource resource) throws IOException {
        String json = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return json;
    }
    private String readTestJson() throws IOException {
        return readJson(testJsonResource);
    }
    
    private String readExpectedJson() throws IOException {
        return readJson(expectedJsonResource);
    }
    
    /**
     * Test of replaceVariables method, of class ExpandVariableCommandLineRunner.
     */
    @Test
    public void testReplaceVariables() throws Exception {
        log.info("replaceVariables");
        
        String jsonString = readTestJson();
        Map<String, Object> properties = instance.getVariablesMap(PROPERTIES);

        String expResult = readExpectedJson();
        String result = instance.replaceVariables(jsonString, properties);
        log.info("result = {}", result);
        
        assertEquals(expResult, result);
    }

    private static final Map<String, String> PROPERTIES = Map.of("file", "myfile", "name", "joe", "age", "18");

    /**
     * Test of getVariablesMap method, of class ExpandVariableCommandLineRunner.
     */
    @Test
    public void testGetVariablesMap() {
        log.info("getVariablesMap");

        Map<String, Object> result = instance.getVariablesMap(PROPERTIES);
        
        assertEquals(2, result.size());
        assertTrue(result.containsKey("sample.data.name"));
        assertTrue(result.containsKey("sample.data.age"));
    }


    
}
