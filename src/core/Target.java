package core;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Target {
    private StringProperty name;
    private StringProperty description;

    public Target(){
        name = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    public void setName(String val){
        this.name.setValue(val);
    }

    public String getName(){
        return this.name.getValue();
    }

    public void setDescription(String val){
        this.description.setValue(val);
    }

    public String getDescription(){
        return this.description.getValue();
    }
}
