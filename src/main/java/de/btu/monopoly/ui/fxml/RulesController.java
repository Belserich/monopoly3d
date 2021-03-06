package de.btu.monopoly.ui.fxml;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import de.btu.monopoly.Global;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author augat
 */
public class RulesController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button backButton;

    @FXML
    private Label labelRegeln;

    @FXML
    private JFXButton allgemeinButton;

    @FXML
    private JFXButton vorDemSpielButton;

    @FXML
    private JFXButton gefaengnisphaseButton;

    @FXML
    private JFXButton wurfphaseButton;

    @FXML
    private JFXButton feldphaseButton;

    @FXML
    private JFXButton aktionsphaseButton;

    @FXML
    private JFXButton haeuserButton;

    @FXML
    private JFXButton hypothekButton;

    @FXML
    private JFXButton auktionenButton;

    @FXML
    private StackPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String image = " -fx-background-image: url('/images/Main_Background.png\') ;\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        backButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    backButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        allgemeinButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    allgemeinButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        vorDemSpielButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    vorDemSpielButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        gefaengnisphaseButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    gefaengnisphaseButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wurfphaseButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    wurfphaseButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        feldphaseButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    feldphaseButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        aktionsphaseButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    aktionsphaseButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        haeuserButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    haeuserButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        hypothekButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    hypothekButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        auktionenButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    auktionenButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(RulesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Animation
        backButton.setOpacity(0);
        labelRegeln.setOpacity(0);
        allgemeinButton.setOpacity(0);
        vorDemSpielButton.setOpacity(0);
        gefaengnisphaseButton.setOpacity(0);
        wurfphaseButton.setOpacity(0);
        feldphaseButton.setOpacity(0);
        aktionsphaseButton.setOpacity(0);
        haeuserButton.setOpacity(0);
        hypothekButton.setOpacity(0);
        auktionenButton.setOpacity(0);

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton2 = new FadeTransition(Duration.millis(500), labelRegeln);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton4 = new FadeTransition(Duration.millis(500), allgemeinButton);
        fadeInButton4.setFromValue(0);
        fadeInButton4.setToValue(1);
        fadeInButton4.playFromStart();

        FadeTransition fadeInButton5 = new FadeTransition(Duration.millis(500), vorDemSpielButton);
        fadeInButton5.setFromValue(0);
        fadeInButton5.setToValue(1);
        fadeInButton5.playFromStart();

        FadeTransition fadeInButton6 = new FadeTransition(Duration.millis(500), gefaengnisphaseButton);
        fadeInButton6.setFromValue(0);
        fadeInButton6.setToValue(1);
        fadeInButton6.playFromStart();

        FadeTransition fadeInButton7 = new FadeTransition(Duration.millis(500), wurfphaseButton);
        fadeInButton7.setFromValue(0);
        fadeInButton7.setToValue(1);
        fadeInButton7.playFromStart();

        FadeTransition fadeInButton8 = new FadeTransition(Duration.millis(500), feldphaseButton);
        fadeInButton8.setFromValue(0);
        fadeInButton8.setToValue(1);
        fadeInButton8.playFromStart();

        FadeTransition fadeInButton9 = new FadeTransition(Duration.millis(500), aktionsphaseButton);
        fadeInButton9.setFromValue(0);
        fadeInButton9.setToValue(1);
        fadeInButton9.playFromStart();

        FadeTransition fadeInButton10 = new FadeTransition(Duration.millis(500), haeuserButton);
        fadeInButton10.setFromValue(0);
        fadeInButton10.setToValue(1);
        fadeInButton10.playFromStart();

        FadeTransition fadeInButton11 = new FadeTransition(Duration.millis(500), hypothekButton);
        fadeInButton11.setFromValue(0);
        fadeInButton11.setToValue(1);
        fadeInButton11.playFromStart();

        FadeTransition fadeInButton12 = new FadeTransition(Duration.millis(500), auktionenButton);
        fadeInButton12.setFromValue(0);
        fadeInButton12.setToValue(1);
        fadeInButton12.playFromStart();

    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {
        if (dialogPane.getChildren().size() > 0) {
            if (dialogPane.getChildren().get(0) instanceof JFXDialog) {
                ((JFXDialog) dialogPane.getChildren().get(0)).close();
            }
        }
        changeScene(new FXMLLoader(getClass().getResource("/fxml/menu_scene.fxml")));
    }

    @FXML
    private void vorDemSpielButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Vor dem Spiel");
        Text text = new Text("Beim Start des Spiels werden zwei W??rfel bereit gelegt.\n"
                + "Jeder Spieler w??rfelt einmal mit beiden W??rfeln.\n"
                + "Die Reihenfolge wird daraufhin ??ber die geworfene Augenzahl festgelegt, wobei der erste Spieler der ist, der die h??chste Augenzahl geworfen hat\n"
                + "und der letzte Spieler der ist, der die niedrigste Augenzahl geworfen hat.\n"
                + "Alle Spieler erhalten ein Startkapital von 1500???.\n"
                + "Jedem Spieler wird au??erdem eine Farbe zugewiesen.\n"
                + "Es beginnen so lange Spielrunden, bis ein endg??ltiger Gewinner feststeht.\n"
                + "In einer Spielrunde durchl??uft jeder aktive Spieler in der festgelegten Reihenfolge eine Rundenphase, bis ein endg??ltiger Gewinner feststeht.\n"
                + "Der Spieler, der momentan an der Reihe ist wird fortan momentaner Spieler genannt.\n"
                + "Ein Mitspieler aus der Sicht des momentanen Spielers wird als Gegner bezeichnet.");

        showDialog(headline, text);
    }

    @FXML
    private void allgemeinButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Allgemein");
        Text text = new Text("Ziel des Spiels ist es, als einziger verbleibender Spieler, Kapital zu besitzen.\n"
                + "F??llt das Eigenkapital von einem Spieler unter null, so scheidet dieser aus dem Spiel aus.\n\n"
                + "Allgemeine Festlegungen:\n"
                + "      Das W??hrungzeichen ist ??? (Euro).\n"
                + "      Die Spielfiguren bewegen sich immer im Uhrzeigersinn, um das Spielbrett.\n"
                + "      Es wird mit zwei W??rfeln gew??rfelt.\n\n"
                + "Die Rundenphase eines Spielers besteht aus folgenden Teilphasen:\n"
                + "      Gef??ngnisphase\n"
                + "      Wurfphase\n"
                + "      Feldphase\n"
                + "      Aktionsphase");

        showDialog(headline, text);
    }

    @FXML
    private void gefaengnisphaseButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Gef??ngnisphase");
        Text text = new Text("In der Gef??ngnisphase hat der Spieler drei Optionen von denen er eine w??hlen muss: Er kann 50??? bezahlen,\n"
                + "eine ???Gef??ngnis Frei???-Karte spielen oder w??rfeln. W??hlt er eine der beiden ersten Optionen und sind diese\n"
                + "erlaubt, so ist er frei und kommt in die n??chste Teilphase. W??hlt der Spieler, zu w??rfeln und w??rfelt er ein\n"
                + "Pasch, so kommt er ebenfalls frei und in die n??chste Teilphase, w??rfelt er kein Pasch und es ist seine dritte\n"
                + "Runde im Gef??ngnis, werden ihm 50??? abgezogen und er kommt ebenfalls frei und in die n??chste Teilphase. Falls\n"
                + "keiner dieser F??lle eintritt beginnt f??r den momentanen Spieler direkt die Aktionsphase.");

        showDialog(headline, text);
    }

    @FXML
    private void wurfphaseButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Wurfphase");
        Text text = new Text("In der Wurfphase werden zwei W??rfel geworfen, ihre Summe ergibt die Anzahl der Felder, um die sich der\n"
                + "momentane Spieler im Uhrzeigersinn bewegt. Kommt der Spieler ??ber das \"LOS\"-Feld w??hrend seines Zuges, so\n"
                + "bekommt er 200??? von der Bank. Hat der momentane Spieler in seiner Wurfphase ein Pasch (zweimal dieselbe\n"
                + "Zahl) geworfen, wechselt dieser nach seiner n??chsten Aktionsphase sofort wieder in die Wurfphase, au??er es ist\n"
                + "sein dritter Pasch in Folge, dann wird seine Spielfigur ins Gef??ngnis gesetzt und sein Zug ist mit sofortiger\n"
                + "Wirkung beendet.");

        showDialog(headline, text);
    }

    @FXML
    private void feldphaseButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Feldphase");
        Text text = new Text("In der Feldphase wird die jeweilige Aktion des Feldes auf dem sich der momentane Spieler befindet ausgef??hrt.\n\n"
                + "Die verschiedenen Feldereignisse sind:\n"
                + "     1. Das Feld geh??rt niemandem und ist k??uflich: Der momentane Spieler kann sich f??r einen Kauf zum\n"
                + "         angegebenen Preis entscheiden, lehnt er den Kauf ab wird es versteigert.\n"
                + "     2. Das Feld ist Stra??e und geh??rt dem Gegner. Der Gegner hat keine Hypothek auf die Stra??e aufgenommen:\n"
                + "         Der momentane Spieler zahlt dem Gegner Miete (der Stra??e / dem Bahnhof / dem Werk zu entnehmen).\n"
                + "     3. Das Feld ist Stra??e und geh??rt dem Gegner. Der Gegner hat eine Hypothek auf die Stra??e aufgenommen:\n"
                + "         Nichts passiert.\n"
                + "     4. Das Feld geh??rt dem momentanen Spieler: Nichts passiert.\n"
                + "     5. Das Feld ist Ereignis- oder Gemeinschaftsfeld: Es wird eine Karte vom entsprechendem Stapel gezogen\n"
                + "         und die jeweilige Aktion durchgef??hrt.\n"
                + "     6. Das Feld ist Einkommens- oder Zusatzsteuerfeld: Der festgelegte Betrag wird an die Bank gezahlt.\n"
                + "     7. Das Feld ist ???Gehen Sie in das Gef??ngnis???-Feld: Der Spieler geht ins Gef??ngnis, bekommt keine 200??? und\n"
                + "         sein Zug ist beendet.\n"
                + "     8. Das Feld ist ???Frei Parken???-Feld: Nichts passiert.\n"
                + "     9. Das Feld ist ???Gef??ngnis???-Feld: Nichts passiert.");

        showDialog(headline, text);
    }

    @FXML
    private void aktionsphaseButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Aktionsphase");
        Text text = new Text("In der Aktionsphase darf der momentane Spieler beliebig viele Hypotheken aufnehmen, beliebig viele Hypotheken\n"
                + "abzahlen, beliebig viele H??user kaufen, beliebig viele H??user verkaufen (beides unter Einhaltung der\n"
                + "Kriterien f??r den Hauskauf) und beliebig viele Handelsanfragen an andere Spieler versenden, jedoch an\n"
                + "jeden Spieler nur einmal pro Rundenphase.");

        showDialog(headline, text);
    }

    @FXML
    private void haeuserButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("H??user");
        Text text = new Text("Die Kriterien f??r den Hauskauf lauten:\n\n"
                + "     1. Das Bauen von H??usern auf einer Stra??e ist erst dann m??glich, wenn der gesamte zugeh??rige Stra??enzug\n"
                + "         im Besitz des momentanen Spielers ist.\n"
                + "     2. Ein Stra??enzug wird gleichm????ig bebaut.\n"
                + "     3. Alle Stra??en des Stra??enzuges m??ssen hypotheksfrei sein.\n"
                + "     4. Es d??rfen nicht mehr als f??nf H??user pro Stra??e gebaut werden. Das f??nfte Haus ersetzt s??mtliche H??user\n"
                + "        der Stra??e durch ein Hotel.\n"
                + "     5. H??user k??nnen zum halben Preis wieder an die Bank verkauft werden. Dies ist auch nur gleichm????ig\n"
                + "        m??glich.");

        showDialog(headline, text);
    }

    @FXML
    private void hypothekButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Hypothek");
        Text text = new Text("Nimmt der momentane Spieler eine Hypothek f??r eine Stra??e auf, so bekommt dieser die H??lfte des Kaufpreises\n"
                + "der Stra??e als Hypothekswert von der Bank gutgeschrieben. Die Stra??e wird mit dem Hypothekswert und zehn\n"
                + "Prozent Zinsen belastet. Voraussetzung f??r die Aufnahme einer Hypothek ist, dass alle Stra??en desselben\n"
                + "Stra??enzugs im Besitz des momentanen Spielers h??user- und hotelfrei sind.");

        showDialog(headline, text);
    }

    @FXML
    private void auktionenButtonAction(ActionEvent event) throws IOException {
        Text headline = new Text("Auktionen");
        Text text = new Text("Versteigerungen und Auktionen beginnen bei einem Mindestwert von 1???. Zu Beginn nehmen alle Spieler\n"
                + "an der Auktion teil. Die Auktion ist beendet, wenn alle Spieler ausgestiegen sind, oder nur noch ein bietender\n"
                + "Spieler ??brig ist, das zu versteigernde Objekt geht dann an den letzten Bieter zum gebotenen Preis. Wird auf ein\n"
                + "Objekt nicht geboten geht es an die Bank. Spieler k??nnen nicht mehr Geld bieten als sie besitzen. Jede Runde\n"
                + "der Auktion beginnt damit, dass s??mtliche Spieler die noch an der Auktion teilnehmen entweder ein h??heres\n"
                + "Angebot machen, als zuletzt gegeben wurde oder aussteigen. Steigt ein Spieler aus nimmt er fortan nicht mehr\n"
                + "an der Auktion teil. Das jeweils h??chste Gebot einer Runde ist das zu ??berbietende Gebot der folgenden Runde.");

        showDialog(headline, text);
    }

    private void showDialog(Text headline, Text text) {

        if (dialogPane.getChildren().size() > 0) {
            if (dialogPane.getChildren().get(0) instanceof JFXDialog) {
                ((JFXDialog) dialogPane.getChildren().get(0)).close();
            }
        }

        headline.setFont(Font.font("System", FontPosture.REGULAR, 16));
        headline.setFill(Color.WHITE);
        text.setFont(Font.font("System", FontPosture.REGULAR, 14));
        text.setFill(Color.WHITE);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(headline);
        content.setBody(text);
        content.setStyle("-fx-background-color: #212121");

        JFXDialog dialog = new JFXDialog(dialogPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton button = new JFXButton("Schlie??en");
        button.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }

        });
        content.setActions(button);
        dialog.show();
    }

    private void changeScene(FXMLLoader loader) {
        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), labelRegeln);
        fadeInButton2.setFromValue(1);
        fadeInButton2.setToValue(0);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton4 = new FadeTransition(Duration.millis(500), allgemeinButton);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();

        FadeTransition fadeInButton5 = new FadeTransition(Duration.millis(500), vorDemSpielButton);
        fadeInButton5.setFromValue(1);
        fadeInButton5.setToValue(0);
        fadeInButton5.playFromStart();

        FadeTransition fadeInButton6 = new FadeTransition(Duration.millis(500), gefaengnisphaseButton);
        fadeInButton6.setFromValue(1);
        fadeInButton6.setToValue(0);
        fadeInButton6.playFromStart();

        FadeTransition fadeInButton7 = new FadeTransition(Duration.millis(500), wurfphaseButton);
        fadeInButton7.setFromValue(1);
        fadeInButton7.setToValue(0);
        fadeInButton7.playFromStart();

        FadeTransition fadeInButton8 = new FadeTransition(Duration.millis(500), feldphaseButton);
        fadeInButton8.setFromValue(1);
        fadeInButton8.setToValue(0);
        fadeInButton8.playFromStart();

        FadeTransition fadeInButton9 = new FadeTransition(Duration.millis(500), aktionsphaseButton);
        fadeInButton9.setFromValue(1);
        fadeInButton9.setToValue(0);
        fadeInButton9.playFromStart();

        FadeTransition fadeInButton10 = new FadeTransition(Duration.millis(500), haeuserButton);
        fadeInButton10.setFromValue(1);
        fadeInButton10.setToValue(0);
        fadeInButton10.playFromStart();

        FadeTransition fadeInButton11 = new FadeTransition(Duration.millis(500), hypothekButton);
        fadeInButton11.setFromValue(1);
        fadeInButton11.setToValue(0);
        fadeInButton11.playFromStart();

        FadeTransition fadeInButton12 = new FadeTransition(Duration.millis(500), auktionenButton);
        fadeInButton12.setFromValue(1);
        fadeInButton12.setToValue(0);
        fadeInButton12.playFromStart();

        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(1);
        fadeInButton1.setToValue(0);
        fadeInButton1.playFromStart();
        fadeInButton1.setOnFinished((event) -> {
            try {
                Global.ref().getMenuSceneManager().changeScene(loader);
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
