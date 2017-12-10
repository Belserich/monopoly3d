package de.btu.monopoly.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author augat
 */
public class PopupController implements Initializable {

    @FXML
    private Label inputLabel;

    @FXML
    private Button inputButton;

    @FXML
    private TextField inputTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void inputAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Lobby
    }

}
