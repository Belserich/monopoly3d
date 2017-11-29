package de.btu.monopoly.ui.chat;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class ChatPanel extends BorderPane {
    
    private TextArea textArea;
    private TextField textField;
    
    public ChatPanel() {
        super();
        init();
    }
    
    private void init() {
        textArea = new TextArea();
        textField = new TextField();
        
        this.setCenter(textArea);
        this.setBottom(textField);
    }
    
    public TextArea getTextArea() {
        return textArea;
    }
    
    public TextField getTextField() {
        return textField;
    }
}
