package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Category;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import org.springframework.beans.factory.annotation.Autowired;


public class EntryDialog extends Dialog {
    private Entry entry = new Entry();
    private boolean saveState = false;

    public EntryDialog(String fileName, Config config, Category category, @Autowired FileHandler fileHandler) {

        TextField restURLFieldTF = new TextField("REST URL");

        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            if(restURLFieldTF.getValue().isEmpty()){
                CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                errorNotification.open();
            }else {
                entry.setRestURL(restURLFieldTF.getValue());
                category.getEntries().add(entry);
                int categoryIndex = config.getServer().getCategories().indexOf(category);
                config.getServer().getCategories().set(categoryIndex, category);
                fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                saveState=true;
                this.close();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(restURLFieldTF, buttonLayout);
        this.add(dialogContentLayout);
    }

    public Entry getEntry() {
        return entry;
    }

    public boolean isSaveState() {
        return saveState;
    }

}
