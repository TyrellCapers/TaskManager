package core;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

public class Main extends Application {

    public static MenuBar menuBar;
    public static TabPane tabPane;
    public static HashMap<String, TaskManager> managers;
    public static HashMap<String, Tab> tabs;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Task.buildStatusMap();

        buildMenuBar(primaryStage);
        buildTabPane();
        buildManagers();
        buildTabs();

        Parent root;

        VBox layout = new VBox();
        layout.getChildren().addAll(menuBar, tabPane);

        primaryStage.setTitle("Task Managers");
        primaryStage.setScene(new Scene(layout, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void buildMenuBar(Stage primaryStage) throws Exception{
        menuBar = new MenuBar();

        //Create menus
        Menu fileMenu = new Menu("File");

        //File Menu Items
        MenuItem newTabItem = new MenuItem("New");
        newTabItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                newTaskManager();
            }
        });

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
                fileChooser.getExtensionFilters().add(extFilter);

                File file = fileChooser.showSaveDialog(primaryStage);

                if(file != null){
                    String selectedTabName = tabPane.getSelectionModel().getSelectedItem().getText();
                    Tab tab = tabs.get(selectedTabName);
                    TaskManager manager = managers.get(selectedTabName);


                    try {
                        String saveContent = manager.saveXML();
                        PrintWriter pw = new PrintWriter(file);
                        pw.write(saveContent);
                        pw.close();

                        tab.setText(file.getName());

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        MenuItem loadItem = new MenuItem("Load");
        loadItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML FILES (*.xml)", "*.xml");
                fileChooser.getExtensionFilters().add(extFilter);

                File file = fileChooser.showOpenDialog(primaryStage);
                if(file != null){
                    loadTaskManager(file);
                }
            }
        });

        fileMenu.getItems().addAll(newTabItem, saveItem, loadItem);

        //Add Menu Bar children
        menuBar.getMenus().addAll(fileMenu);
    }

    public static void buildTabPane(){
        tabPane = new TabPane();
    }

    public static void buildManagers(){
        managers = new HashMap<>();
    }

    public static void buildTabs(){
        tabs = new HashMap<>();
    }

    public static void newTaskManager(){
        TaskManager newManager = new TaskManager();

        boolean placed = false;
        int index = 1;
        String newName = null;
        //Create the New Task Manager Name
        while(!placed){
            newName = "New " + index;
            if(managers.get(newName) == null){
                managers.put(newName, newManager);
                placed = true;
            }
            else{
                index++;
            }
        }

        //Create the New Tab
        Tab newTab = new Tab(newName, newManager.buildLayout());
        tabs.put(newName, newTab);
        tabPane.getTabs().add(newTab);


    }

    public static void loadTaskManager(File file){
        TaskManager newManager = new TaskManager();
        String tabName = null;

        if(managers.get(file.getName()) == null){
            managers.put(file.getName(), newManager);
            tabName = file.getName();
        }
        else{
            managers.put(file.getAbsolutePath(), newManager);
            tabName = file.getAbsolutePath();
        }

        try {
            newManager.loadXML(file);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Tab newTab = new Tab(tabName, newManager.buildLayout());
        tabs.put(tabName, newTab);
        tabPane.getTabs().add(newTab);
    }
}
