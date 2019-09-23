package me.runelocus.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.runelocus.core.Core;
import me.runelocus.threading.RuneLocusThread;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainUI {
    private JFrame frame;
    private JFXPanel jfxPanel = new JFXPanel();
    private Button load, clear;
    private TextField nameText;
    private TableView tableView;
    private TableColumn<String, ThreadFrame> threadName, status;
    private TableColumn<Integer, ThreadFrame> voted, failed;
    private List<RuneLocusThread> threadList = new ArrayList<>();

    /**
     * @author Ethan
     */

    public static void main(String[] args) {
        new MainUI();
    }

    public MainUI() {
        loadGUI();
    }

    private void loadGUI() {
        Platform.runLater(() -> {
            frame = new JFrame("Voting Statistics");
            jfxPanel = new JFXPanel();
            nameText = new TextField();
            nameText.setPromptText("Search by thread?");

            load = new Button("Load/Refresh");
            load.setOnAction(e -> loadThreads());

            clear = new Button("Clear");
            clear.setOnAction(e -> clear());

            tableView = new TableView();
            setColumns();
            tableView.getColumns().addAll(threadName, voted, failed, status);


            HBox hBox = new HBox();
            hBox.getChildren().addAll(nameText, load, clear);
            hBox.setSpacing(25);

            VBox layout = new VBox(5);
            layout.setPadding(new Insets(0, 0, 10, 0));

            layout.getChildren().addAll(tableView, hBox);

            jfxPanel.setScene(new Scene(layout, 375, 300));
            SwingUtilities.invokeLater(() -> {
                frame.add(jfxPanel);
                frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            });
        });
    }

    private void setColumns() {
        threadName = new TableColumn<>("Thread");
        threadName.setCellValueFactory(new PropertyValueFactory<>("threadName"));
        threadName.setMaxWidth(150);

        voted = new TableColumn<>("Voted");
        voted.setCellValueFactory(new PropertyValueFactory<>("voted"));
        voted.setMaxWidth(50);

        failed = new TableColumn<>("Failed");
        failed.setCellValueFactory(new PropertyValueFactory<>("failed"));
        failed.setMaxWidth(50);

        status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setMinWidth(175);


    }

    public void setVisible() {
        frame.setVisible(true);
    }

    private void clear() {
        if (!tableView.getItems().isEmpty())
            tableView.getItems().clear();
        clearText();
    }

    private void clearText() {
        if (!nameText.getText().isEmpty())
            nameText.clear();
    }

    private void loadThreads() {
        tableView.setItems(getThreadFrames());
        clearText();

    }


    private ObservableList<ThreadFrame> getThreadFrames() {
        ObservableList<ThreadFrame> threadFrames = FXCollections.observableArrayList();
        if (nameText.getText().isEmpty()) {
            for (RuneLocusThread t : getThreadList()) {
                if (t != null) {
                    threadFrames.add(new ThreadFrame(t, t.getVoteCount(), t.getFailed(), t.getStartTime(), t.getThreadName(), t.getStatus()));
                }
            }
        }
        return threadFrames;
    }

    public List<RuneLocusThread> getThreadList() {
        return threadList;
    }
}
