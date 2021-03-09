package com.android.tallyman;

public class TallyCard {
    String id;
    public int count;
    public String label;
    public String dateCreated;

    public TallyCard(String id, String label, String dateCreated, int count) {
        this.id = id;
        this.count = count;
        this.label = label;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
