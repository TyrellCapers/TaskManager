package core;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.util.HashMap;
import javafx.scene.layout.HBox;

public class FieldContainer extends HBox {
    public final double LABEL_FIELD_SPACING = 5.0;
    public final double FIELD_FIELD_SPACING = 10.0;

    private HashMap<String, TextField> fieldMap;

    public FieldContainer(){
        this.fieldMap = new HashMap<>();

        this.setSpacing(FIELD_FIELD_SPACING);
    }

    public void addField(String fieldName){
        Label label = new Label(fieldName + ":");
        TextField field = new TextField();

        HBox fieldBox = new HBox();
        fieldBox.setSpacing(LABEL_FIELD_SPACING);
        fieldBox.getChildren().addAll(label, field);

        fieldMap.put(fieldName, field);

        this.getChildren().add(fieldBox);
    }

    public TextField retrieveField(String fieldName){
        return fieldMap.get(fieldName);
    }
}
