package com.example.mgalante.marvelprojectbase.api.entities;

import java.util.List;

/**
 * Created by mgalante on 16/01/17.
 */
public class ResourceList {
    private int available;
    private int returned;
    private String collectionURI;
    private List<SummaryEntity> items;

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getReturned() {
        return returned;
    }

    public void setReturned(int returned) {
        this.returned = returned;
    }

    public String getCollectionURI() {
        return collectionURI;
    }

    public void setCollectionURI(String collectionURI) {
        this.collectionURI = collectionURI;
    }

    public List<SummaryEntity> getItems() {
        return items;
    }

    public void setItems(List<SummaryEntity> items) {
        this.items = items;
    }
}
