/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.springexpandjsonvariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 *
 * @author aar1069
 */
@Component
@Slf4j
public class ExpandVariableCommandLineRunner implements CommandLineRunner {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${sample.data.file}")
    private String dataFile;

    @Value("${sample.data.name}")
    private String sampleName;

    @Value("${sample.data.age}")
    private Integer sampleAge;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    public String replaceVariables(String jsonString, Map<String, Object> properties) {
        StringSubstitutor substitutor = new StringSubstitutor(properties);
        return substitutor.replace(jsonString);
    }

    private static final String PREFIX = "sample.data";
    
    public Map<String, Object> getVariablesMap(Map<String, String> properties) {
        Map<String, Object> variables = new HashMap<>();
        properties.keySet().stream()
                .filter(k -> !k.equals("file"))
                .forEach(k -> variables.put(PREFIX + "." + k, properties.get(k)));
        log.info("variables = {}", variables);
        
        return variables;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("loading data");

        // read the environment for all variables starting with sample.data, and add to a map
        // that will be used for expansion.  This allows new varaibles to be added without 
        // changing code.  see https://stackoverflow.com/questions/47873185/how-to-read-multiple-spring-properties-with-same-prefix-in-java
        Map<String, String> properties = Binder.get(applicationContext.getEnvironment())
                .bind(PREFIX, Bindable.mapOf(String.class, String.class))
                .orElse(Collections.emptyMap());
        properties.keySet().stream()
                .forEach(k -> log.info("property {} with value {}", PREFIX + "." + k, properties.get(k)));

        Map<String, Object> variables = getVariablesMap(properties);

//        Map<String, Object> variables = Map.of("sample.data.name", sampleName, "sample.data.age", sampleAge);
        // read the json file into a string
        Resource resource = resourceLoader.getResource(dataFile);
        String json = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // replace the variables
        String expandedJson = replaceVariables(json, variables);
        log.info("read json {}", expandedJson);

        //  SampleData[] data = objectMapper.readValue(resource.getInputStream(), SampleData[].class);
        //  Stream.of(data).forEach(d -> log.info("read datum {}", d));
        // deserialize
        SampleData[] data2 = objectMapper.readValue(expandedJson, SampleData[].class);
        Stream.of(data2).forEach(d -> log.info("read expanded datum {}", d));
    }

}
