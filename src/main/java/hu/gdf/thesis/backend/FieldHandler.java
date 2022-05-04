package hu.gdf.thesis.backend;

import com.fasterxml.jackson.databind.JsonNode;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.Operation;
import hu.gdf.thesis.model.config.RestField;

import java.util.Locale;

public class FieldHandler {

    private String color;

    public void executeAction(String actionValue) {
        switch (actionValue) {
            case "colorGreen":
                color = ".colorGreen";
                break;
            case "colorYellow":
                color = ".colorYellow";
                break;
            case "colorRed":
                color = ".colorRed";
                break;
            default:
                color = "default";
        }
    }


    public void checkOperator(RestField restField, JsonNode value) {
        for (Operation operation : restField.getOperation()) {
            switch (operation.getOperator()) {
                case "equals":
                    if (value.get(restField.getFieldName()).textValue().equals(operation.getValue())) {
                        executeAction(operation.getAction());
                    }
                    break;
                case "contains":
                    if (value.get(restField.getFieldName()).textValue().contains(operation.getValue())) {
                        executeAction(operation.getAction());
                    }
                    break;
                case "doesNotContain":
                    if (!value.get(restField.getFieldName()).textValue().contains(operation.getValue())) {
                        executeAction(operation.getAction());
                    }
                    break;
                case "greater":
                    if (operation.getValue() != null) {
                        if (Integer.parseInt(value.get(restField.getFieldName()).textValue()) > Integer.parseInt(operation.getValue())) {
                            executeAction(operation.getAction());
                        }
                    }
                    break;
                case "lesser":
                    if (operation.getValue() != null) {
                        if (Integer.parseInt(value.get(restField.getFieldName()).textValue()) < Integer.parseInt(operation.getValue())) {
                            executeAction(operation.getAction());
                        }
                    }
                    break;
                case "startsWith":
                    if (value.get(restField.getFieldName()).textValue().startsWith(operation.getValue())) {
                        executeAction(operation.getAction());
                    }
                    break;
                case "endsWith":
                    if (value.get(restField.getFieldName()).textValue().endsWith((operation.getValue()))) {
                        executeAction(operation.getAction());
                    }
                    break;
                case "true":
                    if (TypeConverter.tryParseBool((value.get(restField.getFieldName()).textValue()))) {
                        boolean entityFieldBoolValue = Boolean.parseBoolean(value.get(restField.getFieldName()).textValue());
                        if (entityFieldBoolValue) {
                            executeAction(operation.getAction());
                        }
                    }
                    break;
                case "false":
                    if (TypeConverter.tryParseBool((value.get(restField.getFieldName()).textValue()))) {
                        boolean entityFieldBoolValue = Boolean.parseBoolean(value.get(restField.getFieldName()).textValue());
                        if (!entityFieldBoolValue) {
                            executeAction(operation.getAction());
                        }
                    }
                    break;
                default:executeAction("default");
            }
        }

    }

    public String getColor() {
        return color;
    }
}

