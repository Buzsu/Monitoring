package hu.gdf.thesis.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.RestField;
import org.springframework.stereotype.Service;


@Service
public class ResponseJsonHandler {
    private static JsonNode listOfData = null;


    public void findFieldValueInJson(String json, RestField restField, JsonNode fieldValue, Response response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        if (jsonNode.has("body")) {
            JsonNode nestedJson = jsonNode.get("body");
            if (nestedJson.has("items")) listOfData = nestedJson.get("items");
        }

        if (jsonNode.has("items")) {
            listOfData = jsonNode.get("items");
        }
        searchForEntity(jsonNode, restField, fieldValue, response);
    }

    public JsonNode searchForEntity(JsonNode node, RestField restField, JsonNode fieldValue, Response response) {
        // naive depth-first search, with recursions

        if (node == null) {
            return null;
        }
        if (node.isArray()) {
            JsonNode parent;
            for (JsonNode n : node) {
                //Exclude this object from json

                parent = getParentNodeForSpecificFields(n);
                //To find the end of entity in JsonResponse with recursion
                //Due to recursion node, value will change. So next element will appear when node value equals with listOfData value

                if (n.has(restField.getFieldName())) {
                    fieldValue = n.get(restField.getFieldName());
                    response.setNode(n.get(restField.getFieldName()));
                }

                if (!n.isContainerNode()) {
                    return null;
                }
                for (JsonNode child : n) {
                    if (child.equals(parent)) {
                        //Not to find fields like state in aggregateMetrics object
                        continue;
                    }
                    if (child.isContainerNode()) {
                        JsonNode childResult = searchForEntity(child, restField, fieldValue, response);
                        if (childResult != null && !childResult.isMissingNode()) {
                            return childResult;
                        }
                    }
                }
            }
        } else {

            if (node.has(restField.getFieldName())) {

                fieldValue = node.get(restField.getFieldName());
                response.setNode(node.get(restField.getFieldName()));

                //If field found, not to search its inner content
                if (node.get(restField.getFieldName()).elements().hasNext()) {
                    return null;
                }
            }


            if (!node.isContainerNode()) {
                return null;
            }

            for (JsonNode child : node) {
                if (child.isContainerNode()) {
                    JsonNode childResult = searchForEntity(child, restField, fieldValue, response);
                    if (childResult != null && !childResult.isMissingNode()) {
                        return childResult;
                    }
                }
            }
        }
        //not found fall through
        return null;
    }

    public static JsonNode getParentNodeForSpecificFields(JsonNode n) {
        if (n.has(AppConstants.AGGREGATEMETRICS_CONST)) {
            return n.get(AppConstants.AGGREGATEMETRICS_CONST);
        } else if (n.has(AppConstants.HEALTH_CONST)) {
            return n.get(AppConstants.HEALTH_CONST);
        }
        return null;
    }
}

