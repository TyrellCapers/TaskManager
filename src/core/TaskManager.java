package core;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class TaskManager {
    private TableView taskTable;
    private FieldContainer taskFields;
    private Button taskAddButton;
    private Button taskRemoveButton;
    private Button increaseStatusButton;
    private Button decreaseStatusButton;
    private Button togglePendingButton;

    private TableView targetTable;
    private FieldContainer targetFields;
    private Button targetAddButton;
    private Button targetRemoveButton;
    private Button setCurrentTargetButton;

    private ListView actionListView;
    private Button executeActionButton;

    private TextArea notesTextArea;

    private Task currentTask;

    public TaskManager(){
        buildTaskTable();
        buildTaskFields();
        buildTaskAddButton();
        buildTaskRemoveButton();
        buildTargetTable();
        buildTargetFields();
        buildTargetAddButton();
        buildTargetRemoveButton();
        buildActionListView();
        buildNotesTextArea();
        buildExecuteActionButton();
        buildSetCurrentTargetButton();
        buildIncreaseStatusButton();
        buildDecreaseStatusButton();
        buildTogglePendingButton();
    }

    public BorderPane buildLayout(){
        BorderPane parent = new BorderPane();

        //Build Task Table area
        HBox hbox1 = new HBox();
        hbox1.setSpacing(10);
        hbox1.getChildren().addAll(taskFields, taskAddButton, taskRemoveButton, increaseStatusButton, decreaseStatusButton, togglePendingButton);

        VBox vbox1 = new VBox();
        vbox1.setSpacing(10);
        vbox1.getChildren().addAll(taskTable, hbox1);

        //Build Target Table Area
        HBox hbox2 = new HBox();
        hbox2.setSpacing(10);
        hbox2.getChildren().addAll(targetFields, targetAddButton, targetRemoveButton, setCurrentTargetButton);

        VBox vbox2 = new VBox();
        vbox2.setSpacing(10);
        vbox2.getChildren().addAll(targetTable, hbox2);

        //Build BorderPane Center
        VBox vbox3 = new VBox();
        vbox3.setSpacing(10);
        vbox3.getChildren().addAll(vbox1, vbox2);

        parent.setCenter(vbox3);

        //Build BorderPane Left
        VBox vbox4 = new VBox();
        vbox4.setSpacing(10);
        vbox4.getChildren().addAll(actionListView, executeActionButton);
        parent.setLeft(vbox4);

        //Build BorderPane Right
        parent.setRight(notesTextArea);

        //Return
        return parent;

    }

    public String saveXML() throws Exception{
        //Create a new xml document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        //Create the TaskManager root element
        Element taskManagerElement = doc.createElement("TaskManager");
        doc.appendChild(taskManagerElement);

        //For each task in the task table
        for(Object o : taskTable.getItems().toArray()){
            Task t = (Task) o;

            //Create a new task element
            Element taskElement = doc.createElement("Task");
            //Set attributes
            taskElement.setAttribute("Name", t.getName());
            taskElement.setAttribute("CurrentTarget", t.getCurrentTarget());
            taskElement.setAttribute("Status", Integer.toString(t.getStatusIndex()));
            taskElement.setAttribute("DueDate", t.getDueDate());
            taskElement.setAttribute("Notes", t.getNotes());

            //Set target elements
            for(Target target : t.getTargets()){
                Element targetElement = doc.createElement("Target");
                targetElement.setAttribute("Name", target.getName());
                targetElement.setAttribute("Description", target.getDescription());
                taskElement.appendChild(targetElement);
            }

            taskManagerElement.appendChild(taskElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(doc);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(outputStream);

        transformer.transform(domSource, streamResult);
        return outputStream.toString();
    }

    public void loadXML(File xmlFile) throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        NodeList taskElements = doc.getElementsByTagName("Task");
        for(int i = 0; i < taskElements.getLength(); i++){
            if(taskElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element taskElement = (Element) taskElements.item(i);

                Task newTask = new Task();
                newTask.setName(taskElement.getAttribute("Name"));
                newTask.setCurrentTarget(taskElement.getAttribute("CurrentTarget"));
                newTask.setStatusIndex(Integer.parseInt(taskElement.getAttribute("Status")));
                newTask.setStatus(Task.convertStatusIndex(newTask.getStatusIndex()));
                newTask.setDueDate(taskElement.getAttribute("DueDate"));
                newTask.setNotes(taskElement.getAttribute("Notes"));

                NodeList targetElements = taskElement.getChildNodes();
                for (int j = 0; j < targetElements.getLength(); j++) {
                    if(targetElements.item(j).getNodeType() == Node.ELEMENT_NODE){
                        Element targetElement = (Element) targetElements.item(j);

                        Target newTarget = new Target();
                        newTarget.setName(targetElement.getAttribute("Name"));
                        newTarget.setDescription(targetElement.getAttribute("Description"));

                        newTask.getTargets().add(newTarget);
                    }
                }

                taskTable.getItems().add(newTask);
            }
        }
    }

    private void buildTaskTable(){
        taskTable = new TableView();

        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, Task>("name"));

        TableColumn currentTargetColumn = new TableColumn("Current Target");
        currentTargetColumn.setCellValueFactory(new PropertyValueFactory<String, Task>("currentTarget"));

        TableColumn statusColumn = new TableColumn("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<String, Task>("status"));

        TableColumn dueDateColumn = new TableColumn("Due Date");
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<String, Task>("dueDate"));

        taskTable.getColumns().addAll(nameColumn, currentTargetColumn, statusColumn, dueDateColumn);

        //When new task is selected...
        taskTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
              //Save the notes of the previous task
              if(currentTask != null){currentTask.setNotes(notesTextArea.getText());}

              //Set currentTask variable to the selected task
              currentTask = (Task) taskTable.getSelectionModel().getSelectedItem();

              //Update notes area with new task
              notesTextArea.setText(currentTask.getNotes());

              //Update the target list with the targets of the new task
              targetTable.setItems(currentTask.getTargets());
            }
        });


    }

    private void buildTargetTable(){
        targetTable = new TableView();

        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, Target>("name"));

        TableColumn descriptionColumn = new TableColumn("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<String, Target>("description"));

        targetTable.getColumns().addAll(nameColumn, descriptionColumn);
    }

    private void buildTaskFields(){
        taskFields = new FieldContainer();

        taskFields.addField("Name");
        taskFields.addField("Due Date");



    }

    private void buildTaskAddButton(){
        taskAddButton = new Button("Add Task");
        taskAddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Add new task
                Task newTask = new Task();
                newTask.setName(taskFields.retrieveField("Name").getText());
                newTask.setDueDate(taskFields.retrieveField("Due Date").getText());

                //newTask.setStatus("Not Started");

                taskTable.getItems().add(newTask);
            }
        });
    }

    private void buildTaskRemoveButton(){
        taskRemoveButton = new Button("Remove Task");
        taskRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Task selectedTask = (Task) taskTable.getSelectionModel().getSelectedItem();
                if(selectedTask != null){
                    taskTable.getItems().remove(selectedTask);
                }
            }
        });
    }

    private void buildTargetFields(){
        targetFields = new FieldContainer();

        targetFields.addField("Name");
        targetFields.addField("Description");


    }

    private void buildTargetAddButton(){
        targetAddButton = new Button("Add Target");
        targetAddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Create and add new target
                if(currentTask != null) {
                    Target newTarget = new Target();

                    newTarget.setName(targetFields.retrieveField("Name").getText());
                    newTarget.setDescription(targetFields.retrieveField("Description").getText());

                    targetTable.getItems().add(newTarget);
                }
            }
        });
    }

    private void buildTargetRemoveButton(){
        targetRemoveButton = new Button("Remove Target");
        targetRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Target selectedTarget = (Target) targetTable.getSelectionModel().getSelectedItem();
                if(selectedTarget != null){
                    targetTable.getItems().remove(selectedTarget);
                }
            }
        });
    }

    private void buildActionListView(){
        actionListView = new ListView();

        actionListView.getItems().add(new Action("Sample Action") {
            @Override
            public void action() {
                System.out.println("Sample Action Called");
            }
        });
    }

    private void buildNotesTextArea(){
        notesTextArea = new TextArea();
    }

    private void buildExecuteActionButton(){
        executeActionButton = new Button("Execute Action");
        executeActionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Action selectedAction = (Action) actionListView.getSelectionModel().getSelectedItem();
                if(selectedAction != null){
                    selectedAction.action();
                }
            }
        });
    }

    private void buildSetCurrentTargetButton(){
        setCurrentTargetButton = new Button("Set Current Target");
        setCurrentTargetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
              Target selectedTarget = (Target) targetTable.getSelectionModel().getSelectedItem();
              if(selectedTarget != null && currentTask != null){
                  System.out.println("Set Current Target");
                  currentTask.setCurrentTarget(selectedTarget.getName());
              }
            }
        });
    }

    private void buildIncreaseStatusButton(){
        increaseStatusButton = new Button("Increase Status");
        increaseStatusButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               if(currentTask != null) {
                   currentTask.increaseStatus();
               }
            }
        });
    }

    private void buildDecreaseStatusButton(){
        decreaseStatusButton = new Button("Decrease Status");
        decreaseStatusButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
              if(currentTask != null) {
                  currentTask.decreaseStatus();
              }
            }
        });
    }

    private void buildTogglePendingButton(){
        togglePendingButton = new Button("Toggle Pending");
        togglePendingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
              if(currentTask != null) {
                  currentTask.togglePending();
              }
            }
        });
    }

}
