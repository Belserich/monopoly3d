/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import de.btu.monopoly.Global;
import de.btu.monopoly.net.chat.GUIChat;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Christian Prinz
 */
public class ChatUi {

    private final VBox wholeChatBox = new VBox();
    private final HBox chatToggleBox = new HBox();
    private boolean fixedToggle = false;

    private void initChatBox() {
        wholeChatBox.getStylesheets().add(this.getClass().getResource("/styles/game_scene.css").toExternalForm());
        JFXTextField chatField = new JFXTextField();
        chatField.setId("chat_enabled");
        chatField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                clickSendMessage(chatField);
            }
        });
        JFXButton sendButton = new JFXButton("Senden");
        sendButton.setId("send_button");
        sendButton.setOnMouseClicked((MouseEvent event) -> {
            clickSendMessage(chatField);
        });

        HBox chatInteractionBox = new HBox(chatField, sendButton);
        HBox.setHgrow(chatField, Priority.ALWAYS);

        TextFlow chatArea = new TextFlow();
        chatArea.setId("chat_enabled");
        ChatObserver obs = new ChatObserver(chatArea);
        GUIChat.getInstance().addObserver(obs);
        ScrollPane scrollChat = new ScrollPane();
        scrollChat.setContent(obs.getTextFlow());
        scrollChat.setId("scroll-pane");
        scrollChat.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollChat.vvalueProperty().bind(chatArea.heightProperty());
        chatArea.setMaxWidth(375);
        wholeChatBox.getChildren().addAll(scrollChat, chatInteractionBox);

        VBox.setVgrow(scrollChat, Priority.ALWAYS);

        wholeChatBox.setPrefWidth(275);
        wholeChatBox.setOnMouseEntered(event -> changeChatSize(true));
        wholeChatBox.setOnMouseExited(event -> changeChatSize(false));
    }

    private void initChatToggleBox() {
        Label toggle = new Label("\n Chat maximieren");
        JFXToggleButton chatButton = new JFXToggleButton();
        chatToggleBox.getChildren().addAll(toggle, chatButton);
        chatButton.setOnMouseReleased((MouseEvent event) -> {
            changeChatSize(chatButton.isSelected());
            fixedToggle = chatButton.isSelected();
        });
        chatButton.setPrefSize(50, 50);

    }

    private void changeChatSize(boolean toggled) {
        if (!fixedToggle) {
            wholeChatBox.setPrefWidth((toggled) ? 350 : 275);
        }
    }

    public VBox getWholeChatBox() {
        return wholeChatBox;
    }

    public HBox getChatToggleBox() {
        return chatToggleBox;
    }

    private void clickSendMessage(TextField chatField) {
        if (!chatField.getText().isEmpty()) {
            GUIChat.getInstance().msg(Global.ref().playerOnClient(), chatField.getText());
        }
        chatField.clear();

    }

    private ChatUi() {
        initChatBox();
        initChatToggleBox();
    }

    public static ChatUi getInstance() {
        return ChatUiHolder.INSTANCE;
    }

    private static class ChatUiHolder {

        private static final ChatUi INSTANCE = new ChatUi();
    }

    private class ChatObserver implements Observer {

        private final TextFlow area;

        ChatObserver(TextFlow textFlow) {
            area = textFlow;
        }

        TextFlow getTextFlow() {
            return area;
        }

        @Override
        public void update(Observable o, Object arg) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    for (int i = 0; i < 5; i++) {
                        Text[] message = (Text[]) arg;
                        area.getChildren().addAll(message[0], message[1]);
                    }
                    return null;
                }
            };
            Platform.runLater(task);
        }

    }
}
