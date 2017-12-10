package de.btu.monopoly.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author augat
 */
public class LobbyController implements Initializable {

    @FXML
    private Label name1Label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name1Label.setText("Test");
    }

}
