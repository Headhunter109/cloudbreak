package com.sequenceiq.provisioning.controller.json;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.provisioning.controller.validation.ValidProvisionRequest;
import com.sequenceiq.provisioning.domain.CloudPlatform;

@ValidProvisionRequest
public class TemplateJson implements JsonEntity {

    private Long id;
    private CloudPlatform cloudPlatform;
    @Size(max = 20)
    private String name;
    private Map<String, Object> parameters = new HashMap<>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public CloudPlatform getCloudPlatform() {
        return cloudPlatform;
    }

    public void setCloudPlatform(CloudPlatform type) {
        this.cloudPlatform = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

}
