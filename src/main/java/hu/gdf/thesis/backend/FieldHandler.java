package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Operation;

import java.util.Locale;

public class FieldHandler {

    public static void executeAction(String actionValue) {
        switch (actionValue) {
            case "colorGreen":
                break;
            case "colorYellow":
                break;
            case "colorRed":
                break;
        }

    }

    /*public static void checkOperator(Operation operation) {

        switch (operation.getOperator()) {
            case "equals":
                if (entityField.getValue().equalsIgnoreCase(operation.getValue())) {
                    executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                }
                break;
            case "contains":
                if (entityField.getValue().contains(operation.getValue())) {
                    executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                }
                break;
            case "doesNotContain":
                if (!entityField.getValue().contains(operation.getValue())) {
                    executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                }
                break;
            case "greater":
                if (operation.getValue() != null) {
                    if (TypeConverter.TryParseInt(old.get(index).getValue()) && TypeConverter.TryParseInt(entityField.getValue()) && (Integer.parseInt(old.get(index).getValue()) > Integer.parseInt(entityField.getValue()))) {
                        executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                    }
                }
                break;
            case "lesser":
                if (operation.getValue() != null) {
                    if (TypeConverter.TryParseInt(old.get(index).getValue()) && TypeConverter.TryParseInt(entityField.getValue()) && (Integer.parseInt(old.get(index).getValue()) < Integer.parseInt(entityField.getValue()))) {
                        executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                    }
                }
                break;
            case "startsWith":
                if (entityField.getValue().startsWith(operation.getValue())) {
                    executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                }
                break;
            case "endsWith":
                if (entityField.getValue().endsWith(operation.getValue())) {
                    executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                }
                break;
            case "true":
                if (TypeConverter.TryParseBool((entityField.getValue()))) {
                    boolean entityFieldBoolValue = Boolean.parseBoolean(entityField.getValue());
                    if (entityFieldBoolValue) {
                        executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                    }
                }
                break;
            case "false":
                if (TypeConverter.TryParseBool((entityField.getValue()))) {
                    boolean entityFieldBoolValue = Boolean.parseBoolean(entityField.getValue());
                    if (!entityFieldBoolValue) {
                        executeAction(rowsOfEntity, index, operation.getAction(), alertEntity, callerType, operation.getValue());
                    }
                }
                break;
        }

    }*/
}

