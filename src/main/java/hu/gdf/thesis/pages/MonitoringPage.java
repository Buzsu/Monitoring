package hu.gdf.thesis.pages;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.OperationHandler;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Consumer;


@Route("monitoring")
public class MonitoringPage extends VerticalLayout {
    static Config config = new Config();

    public MonitoringPage(@Autowired FileHandler fileHandler, RestClient restClient, OperationHandler operationHandler) {
        this.add(new AppHeader());

        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the file you wish to add a monitoring category to.");

        Grid<Response> monitoringGrid = new Grid<>(Response.class, false);
        Grid.Column<Response> serverHostColumn = monitoringGrid.addColumn(Response::getHostName).setHeader("Server Host").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> categoryColumn = monitoringGrid.addColumn(Response::getCategoryType).setHeader("Category").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> restURLColumn = monitoringGrid.addColumn(Response::getRestURL).setHeader("REST URL").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> fieldPathColumn = monitoringGrid.addColumn(Response::getFieldPath).setHeader("Field").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> fieldValueColumn = monitoringGrid.addColumn(Response::getFieldValue).setHeader("Value").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        monitoringGrid.setClassNameGenerator(Response::getColor);


        fileSelect.addValueChangeListener(e -> {
            config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(String.valueOf(fileSelect.getValue())), Config.class);

            List<Response> responseList = new ArrayList<>();

            buildResponseList(config, responseList, restClient, operationHandler);

            GridListDataView<Response> responseDataView = monitoringGrid.setItems(responseList);

            Filter filter = new Filter(responseDataView);

            monitoringGrid.getHeaderRows().clear();
            HeaderRow headerRow = monitoringGrid.appendHeaderRow();

            headerRow.getCell(serverHostColumn).setComponent(
                    createFilterHeader("Server Host", filter::setServerHost));
            headerRow.getCell(categoryColumn).setComponent(
                    createFilterHeader("Category", filter::setCategory));
            headerRow.getCell(restURLColumn).setComponent(
                    createFilterHeader("REST URL", filter::setRestURL));
            headerRow.getCell(fieldPathColumn).setComponent(
                    createFilterHeader("Field", filter::setFieldPath));
            headerRow.getCell(fieldValueColumn).setComponent(
                    createFilterHeader("Value", filter::setFieldValue));
        });

        VerticalLayout pageLayout = new VerticalLayout(fileSelect, monitoringGrid);
        this.add(pageLayout);
    }

    public void buildResponseList(Config config, List<Response> responseList, RestClient restClient, OperationHandler operationHandler) {

        Server server = config.getServer();
        for (Category category : server.getCategories()) {
            for (Entry entry : category.getEntries()) {

                String responseJson = restClient.restCall(server, entry);

                for (RestField restField : entry.getRestFields()) {

                    DocumentContext jsonContext = JsonPath.parse(responseJson);
                    if (!(jsonContext.read(restField.getFieldPath()) instanceof JSONArray)) {
                        String fieldValue = jsonContext.read(restField.getFieldPath());
                        Response response = new Response();
                        response.setHostName(server.getHost() + " : " + server.getPort());
                        response.setCategoryType(category.getType());
                        response.setRestURL(entry.getRestURL());
                        response.setFieldPath(restField.getFieldPath());
                        response.setFieldValue(fieldValue);
                        for (Operation operation : restField.getOperation()) {

                            operationHandler.checkOperation(operation, fieldValue);

                            if (operationHandler.isCheckState()) {
                                response.setColor(operationHandler.getColor());
                            }
                        }
                        responseList.add(response);
                    }

                }
            }
        }
    }

    private static Component createFilterHeader(String labelText,
                                                Consumer<String> filterChangeConsumer) {
        TextField filterTF = new TextField();
        filterTF.setValueChangeMode(ValueChangeMode.EAGER);
        filterTF.setClearButtonVisible(true);
        filterTF.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filterTF.setWidthFull();
        filterTF.getStyle().set("max-width", "100%");
        filterTF.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout filterLayout = new VerticalLayout(filterTF);
        filterLayout.getThemeList().clear();
        filterLayout.getThemeList().add("spacing-xs");

        return filterLayout;
    }

    public static class Filter {
        private final GridListDataView<Response> responseDataView;

        private String serverHost;
        private String category;
        private String restURL;
        private String fieldPath;
        private String fieldValue;

        public Filter(GridListDataView<Response> responseDataView) {
            this.responseDataView = responseDataView;
            this.responseDataView.addFilter(this::test);
        }

        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
            this.responseDataView.refreshAll();
        }

        public void setCategory(String category) {
            this.category = category;
            this.responseDataView.refreshAll();
        }

        public void setRestURL(String restURL) {
            this.restURL = restURL;
            this.responseDataView.refreshAll();
        }

        public void setFieldPath(String fieldPath) {
            this.fieldPath = fieldPath;
            this.responseDataView.refreshAll();
        }

        public void setFieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
            this.responseDataView.refreshAll();
        }

        public boolean test(Response response) {
            boolean matchesServerHost = matches(response.getHostName(), serverHost);
            boolean matchesCategory = matches(response.getCategoryType(), category);
            boolean matchesRestURL = matches(response.getRestURL(), restURL);
            boolean matchesFieldPath = matches(response.getFieldPath(), fieldPath);
            boolean matchesFieldValue = matches(response.getFieldValue(), fieldValue);

            return matchesServerHost && matchesCategory && matchesRestURL && matchesFieldPath && matchesFieldValue;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value
                    .toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}