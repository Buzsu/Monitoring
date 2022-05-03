package hu.gdf.thesis.pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.ResponseJsonHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("monitoring")
public class MonitoringPage extends VerticalLayout {
    static Config config = new Config();
    public MonitoringPage(@Autowired FileHandler fileHandler, RestClient restClient) {
        this.add(new AppHeader());

        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the file you wish to add a monitoring category to.");

        Grid<Response> monitoringGrid = new Grid<>(Response.class, false);
        monitoringGrid.addColumn(Response::getHostName).setHeader("Server Host").setSortable(true);
        monitoringGrid.addColumn(Response::getCategoryType).setHeader("Category").setSortable(true);
        monitoringGrid.addColumn(Response::getRestURL).setHeader("REST URL").setSortable(true);
        monitoringGrid.addColumn(Response::getField).setHeader("Fields & Values").setSortable(true);


        fileSelect.addValueChangeListener(e-> {
            config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(String.valueOf(fileSelect.getValue())),Config.class);

            List<Response> responseList = new ArrayList<>();
            List<RestField> restFieldList = new ArrayList<>();
            List<String> fieldValuePairs = new ArrayList<>();

            buildResponseList(config, responseList, restFieldList, fieldValuePairs, restClient);

            GridListDataView<Response> responseDataView = monitoringGrid.setItems(responseList);
        });

        VerticalLayout pageLayout = new VerticalLayout(fileSelect, monitoringGrid);
        this.add(pageLayout);
    }

    public void buildResponseList(Config config, List<Response> responseList, List<RestField> restFieldList, List<String> fieldValuePairs, RestClient restClient) {
        Server server = config.getServer();
        for(Category category : server.getCategories()) {
            for(Entry entry : category.getEntries()) {
                String responseJson = restClient.restCall(server, entry);
                for(RestField restField : entry.getRestFields()) {
                    restFieldList.add(restField);
                }
                ResponseJsonHandler responseJsonHandler = new ResponseJsonHandler();
                try {
                    responseJsonHandler.findFieldValueInJson(responseJson, restFieldList, fieldValuePairs);
                    for(String pairs : fieldValuePairs){
                        Response response = new Response();
                        response.setHostName(server.getHost()+":"+server.getPort());
                        response.setCategoryType(category.getType());
                        response.setRestURL(entry.getRestURL());
                        response.setField(pairs);
                        response.setRestFieldList(restFieldList);
                        responseList.add(response);

                    }
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
