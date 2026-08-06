package com.company.issuebot.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "issue-bot.db")
public class DbAccessProperties {

    @NotBlank
    private String mode = "metadata-only";

    private List<String> allowedSchemas = List.of();
    private List<String> blockedObjects = List.of();
    private boolean allowProcedures = false;
    private boolean enableTableSampling = false;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<String> getAllowedSchemas() {
        return allowedSchemas;
    }

    public void setAllowedSchemas(List<String> allowedSchemas) {
        this.allowedSchemas = allowedSchemas;
    }

    public List<String> getBlockedObjects() {
        return blockedObjects;
    }

    public void setBlockedObjects(List<String> blockedObjects) {
        this.blockedObjects = blockedObjects;
    }

    public boolean isAllowProcedures() {
        return allowProcedures;
    }

    public void setAllowProcedures(boolean allowProcedures) {
        this.allowProcedures = allowProcedures;
    }

    public boolean isEnableTableSampling() {
        return enableTableSampling;
    }

    public void setEnableTableSampling(boolean enableTableSampling) {
        this.enableTableSampling = enableTableSampling;
    }
}
