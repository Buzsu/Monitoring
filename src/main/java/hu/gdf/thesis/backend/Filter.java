package hu.gdf.thesis.backend;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import hu.gdf.thesis.model.Response;

public class Filter {
    private final GridListDataView<Response> responseDataView;

    private String fullName;
    private String email;
    private String profession;

    public Filter(GridListDataView<Response> responseDataView) {
        this.responseDataView = responseDataView;
        //this.responseDataView.addFilter(this::test);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.responseDataView.refreshAll();
    }

    public void setEmail(String email) {
        this.email = email;
        this.responseDataView.refreshAll();
    }

    public void setProfession(String profession) {
        this.profession = profession;
        this.responseDataView.refreshAll();
    }

    /*public boolean test(Response response) {
        boolean matchesFullName = matches(response.getFullName(), fullName);
        boolean matchesEmail = matches(response.getEmail(), email);
        boolean matchesProfession = matches(response.getProfession(),
                profession);

        return matchesFullName && matchesEmail && matchesProfession;
    }*/

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value
                .toLowerCase().contains(searchTerm.toLowerCase());
    }
}