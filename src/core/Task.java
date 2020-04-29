package core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class Task {
    private StringProperty name;
    private StringProperty currentTarget;
    private StringProperty status;
    private StringProperty dueDate;
    private StringProperty notes;

    private IntegerProperty statusIndex;
    private ObservableList<Target> targets;

    private static HashMap<Integer, String> statusMap = new HashMap<>();

    //TODO status_max,status_min should be derived, not stored
    public static final int STATUS_MAX = 2;
    public static final int STATUS_MIN = 0;

    public static void buildStatusMap(){
        //Note: Never map a status to -1
        statusMap.put(-2, "Pending");
        statusMap.put(0, "Not Started");
        statusMap.put(1, "In Progress");
        statusMap.put(2, "Completed");
    }

    public Task(){
        this.name = new SimpleStringProperty();
        this.currentTarget = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.dueDate = new SimpleStringProperty();
        this.notes = new SimpleStringProperty();


        this.statusIndex = new SimpleIntegerProperty();
        this.statusIndex.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                status.setValue(statusMap.get(statusIndex.getValue()));
            }
        });
        this.statusIndex.setValue(-1);
        this.statusIndex.setValue(0);

        targets = FXCollections.observableArrayList();
        notes.setValue("");


    }

    public void setName(String val){
        this.name.setValue(val);
    }

    public String getName(){
        return this.name.getValue();
    }

    public StringProperty nameProperty(){
        return this.name;
    }

    public void setCurrentTarget(String val){
        this.currentTarget.setValue(val);
    }

    public String getCurrentTarget(){
        return this.currentTarget.getValue();
    }

    public StringProperty currentTargetProperty(){
        return this.currentTarget;
    }

    public void setStatus(String val){
        this.status.setValue(val);
    }

    public String getStatus(){
        this.status.setValue(statusMap.get(statusIndex.getValue()));
        return this.status.getValue();
    }

    public StringProperty statusProperty(){
        return this.status;
    }

    public void setDueDate(String val){
        this.dueDate.setValue(val);
    }

    public String getDueDate(){
        return this.dueDate.getValue();
    }

    public StringProperty dueDateProperty(){
        return this.dueDate;
    }

    public void setNotes(String val){this.notes.setValue(val);}

    public String getNotes(){
        return this.notes.getValue();
    }

    public StringProperty notesProperty(){
        return this.notes;
    }

    public int getStatusIndex(){
        return this.statusIndex.getValue();
    }

    public void setStatusIndex(int val){
        this.statusIndex.setValue(val);
    }

    public ObservableList<Target> getTargets(){
        return this.targets;
    }

    public void increaseStatus(){
        int originalVal = this.statusIndex.getValue();
        int newVal = originalVal + 1;

        if(newVal <= STATUS_MAX && newVal >= STATUS_MIN){
            this.statusIndex.setValue(newVal);
        }

    }

    public void decreaseStatus(){
        int originalVal = this.statusIndex.getValue();
        int newVal = originalVal - 1;
        if(newVal <= STATUS_MAX && newVal >= STATUS_MIN){
            this.statusIndex.setValue(newVal);
        }
    }

    public void togglePending(){
        if(this.statusIndex.getValue() != -2){
            this.statusIndex.setValue(-2);
        }
        else{
            this.statusIndex.setValue(1);
        }
    }

    public static String convertStatusIndex(int index){
        return statusMap.get(index);
    }
}
