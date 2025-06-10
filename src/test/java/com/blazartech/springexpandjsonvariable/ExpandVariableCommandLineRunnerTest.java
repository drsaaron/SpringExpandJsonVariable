/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.blazartech.springexpandjsonvariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.context.ApplicationContext;
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

    private String readJson(Resource resource) {
        try {
            String json = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return json;
        } catch (IOException e) {
            throw new RuntimeException("error reading json file: " + e.getMessage(), e);
        }
    }

    private String readTestJson() {
        return readJson(testJsonResource);
    }

    private String readExpectedJson() {
        return readJson(expectedJsonResource);
    }

    /**
     * Test of replaceVariables method, of class
     * ExpandVariableCommandLineRunner.
     */
    @Test
    public void testReplaceVariables() {
        log.info("replaceVariables");

        // json paths to the name and age variable elements
        String variableNamePath = "$[1].name";
        String variableAgePath = "$[1].age";

        // read the test data and verify nothing is expanded
        String jsonString = readTestJson();
        DocumentContext documentContext = JsonPath.parse(jsonString);
        assertEquals("${sample.data.name}", documentContext.read(variableNamePath));
        assertEquals("${sample.data.age}", documentContext.read(variableAgePath));

        Map<String, Object> properties = instance.getVariablesMap(PROPERTIES, "sample.data");

        // read the expected json
        String expResult = readExpectedJson();

        // do the deed and expand
        String result = instance.replaceVariables(jsonString, properties);
        log.info("result = {}", result);

        // the expanded string should match the hard-coded expanded string
        assertEquals(expResult, result);

        /* parse the json and check specific fields.  we've already compared the final
           json string, but this demonstrates the use of jsonpath.
         */
        documentContext = JsonPath.parse(result);
        String secondName = documentContext.read(variableNamePath);
        int secondAge = documentContext.read(variableAgePath, Integer.class);
        assertEquals(PROPERTIES.get("name"), secondName);
        assertEquals(Integer.valueOf(PROPERTIES.get("age")), secondAge);
    }

    private static final Map<String, String> PROPERTIES = Map.of("file", "myfile", "name", "joe", "age", "18");

    /**
     * Test of getVariablesMap method, of class ExpandVariableCommandLineRunner.
     */
    @Test
    public void testGetVariablesMap() {
        log.info("getVariablesMap");

        Map<String, Object> result = instance.getVariablesMap(PROPERTIES, "sample.data");

        assertEquals(2, result.size());
        assertTrue(result.containsKey("sample.data.name"));
        assertTrue(result.containsKey("sample.data.age"));
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testGetEnvironmentProperties() {
        log.info("getEnvironmentProperties");

        Map<String, String> properties = instance.getEnvironmentProperties(applicationContext, "unittest.sample.data");
        assertNotNull(properties);
        assertFalse(properties.isEmpty());
        assertEquals(7, properties.size());
        assertTrue(properties.containsKey("extraProperty1"));
        assertEquals("extra1", properties.get("extraProperty1"));
    }

}
