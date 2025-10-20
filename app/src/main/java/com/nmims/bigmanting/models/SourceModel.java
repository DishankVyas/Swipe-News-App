package com.nmims.bigmanting.models;

import java.io.Serializable;

/**
 * Model for Article Source
 * Represents the news source (e.g., CNN, BBC, TechCrunch)
 */
public class SourceModel implements Serializable {

    private String id;
    private String name;

    public SourceModel() {
    }

    public SourceModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
