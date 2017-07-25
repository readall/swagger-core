package io.swagger.oas.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.oas.web.OpenAPIConfig;
import io.swagger.oas.web.OpenAPIConfigBuilder;
import io.swagger.util.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationOpenApiConfigBuilder implements OpenAPIConfigBuilder {

    protected URL configLocation;

    // TODO rename / solve
    public Map<String, OpenApiConfiguration> buildMultiple(String defaultId) {

        if (configLocation == null) {
            //TODO
            //throw new RuntimeException("null location");
            return null;
        }

        // get file as string (for the moment, TODO use commons config)
        // load from classpath etc, look from file..
        // TODO multiple formats, yaml..
        try {
            Map<String, OpenApiConfiguration> configurationMap = new HashMap<>();

            String configAsString = readUrl(configLocation);
            // TODO handle different formats
            // TODO use urls as kyes, but need to resolve the url with placeholders and use that?
            // TODO we have multiple base paths in 3.0? how to handle?
            // TODO use urls as kyes, but need to resolve the url with placeholders and use that?
            // check if not array
            JsonNode configNode = Json.mapper().readTree(configAsString);
            final List<OpenApiConfiguration> configurations;
            if (configNode.isArray()) {
                configurations = Json.mapper().readValue(configAsString, new TypeReference<List<OpenApiConfiguration>>() {
                });
            } else {
                OpenApiConfiguration config = Json.mapper().readValue(configAsString, OpenApiConfiguration.class);
                configurations = Collections.singletonList(config);
            }

            for (OpenApiConfiguration config : configurations) {
                configurationMap.put((config.getId() == null) ? defaultId : config.getId(), config);
            }
            return configurationMap;

        } catch (Exception e) {
            // TODO
            e.printStackTrace();
            //throw new RuntimeException("exception reading config", e);
            return null;
        }
    }


    @Override
    public OpenAPIConfig build() {
        if (configLocation == null) {
            // TODO
            //throw new RuntimeException("null location");
            return null;
        }
        // get file as string (for the moment, TODO use commons config)
        // load from classpath etc, look from file..
        try {

            String configAsString = readUrl(configLocation);
            // TODO handle different formats
            // TODO use urls as kyes, but need to resolve the url with placeholders and use that?
            OpenApiConfiguration configuration = Json.mapper().readValue(configAsString, OpenApiConfiguration.class);
            return configuration;

        } catch (Exception e) {
            // TODO
            e.printStackTrace();
            return null;
            //throw new RuntimeException("exception reading config", e);
        }
    }

    public URL getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(URL configLocation) {
        this.configLocation = configLocation;
    }

    public LocationOpenApiConfigBuilder configLocation(URL configLocation) {
        this.configLocation = configLocation;
        return this;
    }

    private static String readUrl(URL url) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine).append("\n");
        }
        in.close();
        return sb.toString();
    }

}
