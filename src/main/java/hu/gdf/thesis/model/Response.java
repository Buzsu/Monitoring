package hu.gdf.thesis.model;

import com.fasterxml.jackson.databind.JsonNode;
import hu.gdf.thesis.backend.FieldHandler;
import hu.gdf.thesis.model.config.RestField;

public class Response {
    private String hostName;
    private String categoryType;
    private String restURL;
    private RestField restField;
    private JsonNode value;
    private String fieldValuePair;
    private String color;

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
        return fieldValuePair;
    }

    public void setField(String fields) {
        this.fieldValuePair = fields;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public RestField getRestField() {
        return restField;
    }

    public void setRestField(RestField restField) {
        this.restField = restField;
    }

    public String getFieldValuePair() {
        return fieldValuePair;
    }

    public JsonNode getNode() {
        return value;
    }

    public void setNode(JsonNode value) {
        this.value = value;
    }

    public void setFieldValuePair() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(restField.getFieldName());
        stringBuilder.append(" : ");
        if (value.isTextual()){
            stringBuilder.append(value.textValue());
        } else {
            stringBuilder.append(value);
        }
        fieldValuePair = String.valueOf(stringBuilder);
    }

    public String getColor() {

        return color;
    }

    public void setColor() {

        FieldHandler fieldHandler = new FieldHandler();
        fieldHandler.checkOperator(restField, value);
        this.color = fieldHandler.getColor();
    }

}
