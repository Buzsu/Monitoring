package hu.gdf.thesis.pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.ResponseJsonHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Consumer;

//@CssImport(value = ".styles/vaadin-grid-styling.css/", themeFor = "vaadin-grid")
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
        monitoringGrid.addColumn(Response::getFieldValuePair).setHeader("Fields & Values").setSortable(true).setClassNameGenerator(Response::getColor);


        fileSelect.addValueChangeListener(e-> {
            config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(String.valueOf(fileSelect.getValue())),Config.class);

            List<Response> responseList = new ArrayList<>();
            List<RestField> restFieldList = new ArrayList<>();

            buildResponseList(config, responseList, restClient);

            GridListDataView<Response> responseDataView = monitoringGrid.setItems(responseList);
            //Filter filter = new Filter(responseDataView);


        });

        VerticalLayout pageLayout = new VerticalLayout(fileSelect, monitoringGrid);
        this.add(pageLayout);
    }

    public void buildResponseList(Config config, List<Response> responseList, RestClient restClient) {

        Server server = config.getServer();
        for(Category category : server.getCategories()) {
            for(Entry entry : category.getEntries()) {
                String responseJson = restClient.restCall(server, entry);

                ResponseJsonHandler responseJsonHandler = new ResponseJsonHandler();
                for(RestField restField : entry.getRestFields()) {
                    try {
                        //ObjectMapper objectMapper = new ObjectMapper();
                        //JsonNode fieldValue = objectMapper.readTree("{}");
                        Response response = new Response();
                        JsonNode fieldValue = null;
                        response.setHostName(server.getHost() + server.getPort());
                        response.setCategoryType(category.getType());
                        response.setRestURL(entry.getRestURL());
                        responseJsonHandler.findFieldValueInJson(responseJson, restField, fieldValue, response);
                        response.setRestField(restField);
                        response.setFieldValuePair();
                        responseList.add(response);
                    } catch (JsonProcessingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    private static Component createFilterHeader(String labelText,
                                                Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField filterTF = new TextField();
        filterTF.setValueChangeMode(ValueChangeMode.EAGER);
        filterTF.setClearButtonVisible(true);
        filterTF.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filterTF.setWidthFull();
        filterTF.getStyle().set("max-width", "100%");
        filterTF.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, filterTF);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

}
