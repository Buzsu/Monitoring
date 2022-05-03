package hu.gdf.thesis.model;

import hu.gdf.thesis.model.config.Operation;
import hu.gdf.thesis.model.config.RestField;

import java.util.ArrayList;
import java.util.List;

public class Response {
    private String hostName;
    private String categoryType;
    private String restURL;
    private String field;
    private List<RestField> restFieldList = new ArrayList<>();

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public String getField() {
        return field;
    }

    public void setField(String fields) {
        this.field = fields;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public List<RestField> getRestFieldList() {
        return restFieldList;
    }

    public void setRestFieldList(List<RestField> restFieldList) {
        this.restFieldList = restFieldList;
    }
}
