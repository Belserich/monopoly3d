/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.Global;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.chat.GUIChat;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class ChatAi {

    private static final Logger LOGGER = Logger.getLogger(HardKi.class.getCanonicalName());

    static void buyStreetMessage(Player ki, boolean choice) {
        Random ran = new Random();
        String mess = "";

        if (choice) {
            switch (ran.nextInt(4) + 1) {
                case 1:
                    mess += "Die kauf ich!";
                    break;
                case 2:
                    mess += "Die will ich haben!";
                    break;
                case 3:
                    mess += "Die Straße wird mir gut Geld einspielen";
                    break;
                case 4:
                    mess += "Darf ich vorstellen: Meine neue Straße";
                    break;
            }
        }
        else {
            switch (ran.nextInt(4) + 1) {
                case 1:
                    mess += "Äh, will die jemand haben?";
                    break;
                case 2:
                    mess += "Die will ich nicht!";
                    break;
                case 3:
                    mess += "Die Straße ist doof";
                    break;
                case 4:
                    mess += "Ich will eine andere Straße haben, darf ich?";
                    break;
            }
        }
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().msgLocal(ki, mess);
        }
    }

    static void continueAuctionMessage(Player ki, int newPrice) {
        Random ran = new Random();
        String mess = "";

        switch (ran.nextInt(4) + 1) {
            case 1:
                mess += "Da geht noch was! Und zwar " + newPrice + "€";
                break;
            case 2:
                mess += "Ich setz mal " + newPrice + "€";
                break;
            case 3:
                mess += "Ich biete " + newPrice + "€";
                break;
            case 4:
                mess += newPrice + "€";
                break;
        }
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().msgLocal(ki, mess);
        }
    }

    static void exitAuctionMessage(Player ki) {
        Random ran = new Random();
        String mess = "";

        switch (ran.nextInt(3) + 1) {
            case 1:
                mess += "Puh! Da bin ich raus!";
                break;
            case 2:
                mess += "Mehr kann ich nicht erübrigen";
                break;
            case 3:
                mess += "Langsam wirds mir zu teuer";
                break;
        }
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().msgLocal(ki, mess);
        }
    }

    static void tradeResultMessage(Player ki, int balance, int MINIMUM_ACCEPT_AMOUNT) {
        Random ran = new Random();
        String mess = "";

        if (balance < (MINIMUM_ACCEPT_AMOUNT * 6)) {           // viel zu teuer
            switch (ran.nextInt(7) + 1) {
                case 1:
                    mess += "Wow, das lohnt sich gar nicht für mich!";
                    break;
                case 2:
                    mess += "Wie bitte?! das ist Abzocke!";
                    break;
                case 3:
                    mess += "Du hast wohl einen Clown gefühstückt. Vergiss es!";
                    break;
                case 4:
                    mess += "Ein unverschämtes Angebot! Nein, danke.";
                    break;
                case 5:
                    mess += "Zu diesem Angebot sag ich ganz klar: #!?%&§?*##!!1!";
                    break;
                case 6:
                    mess += "Soll ich dir vielleicht noch das LOS-Feld schenken?";
                    break;
                case 7:
                    mess += "Du bekommst heute leider keine Rose von mir.";
                    break;
                default:
                    mess += "HALT STOPP!! Es blEibt alles so wieS hier is und es giB hetz hier nichts dran rütteln!!1!";
            }
        }
        else if (balance < MINIMUM_ACCEPT_AMOUNT) {             // zu teuer
            switch (ran.nextInt(5) + 1) {
                case 1:
                    mess += "Sorry, aber das lohnt sich nicht für mich.";
                    break;
                case 2:
                    mess += "Vielleicht, wenn du noch ein bisschen Geld drauflegst";
                    break;
                case 3:
                    mess += "So richtig überzeugt mich dein Angebot nicht!";
                    break;
                case 4:
                    mess += "Hm, das scheint mir nicht so lukrativ";
                    break;
                case 5:
                    mess += "Mach mir ein besseres Angebot und wir sind im Geschäft.";
                    break;
                default:
                    mess += "Mein Buchhalter hat mir empfohlen, dieses Geschäft nicht abzuschließen";
            }
        }
        else if (balance < (Math.sqrt(Math.pow(MINIMUM_ACCEPT_AMOUNT, 2)) * 6)) {      // okay
            switch (ran.nextInt(5) + 1) {
                case 1:
                    mess += "Es war mir eine Ehre mit ihnen Geschäfte zu machen";
                    break;
                case 2:
                    mess += "Ich denke das ist nur Fair. Ich nehme an.";
                    break;
                case 3:
                    mess += "Sieht so aus als wären wir im Geschäft.";
                    break;
                case 4:
                    IOService.sleep(1000);
                    mess += "Nach reichlicher Überlegung nehme ich das Angebot an";
                    break;
                case 5:
                    mess += "Das Angebot ist soweit okay. Da gehe ich mit";
                    break;
                default:
                    mess += "TOP Lieferung, TOP Ware, TOP eBAYER. Gerne wieder - verifizierter Kauf";
            }
        }
        else {                                                  // sehr gut
            switch (ran.nextInt(5) + 1) {
                case 1:
                    mess += "Das ist ein Angebot, was ich nicht ablehnen kann!";
                    break;
                case 2:
                    mess += "Ich liebe es wenn ein Plan aufgeht!";
                    break;
                case 3:
                    mess += "Ja ist denn heut schon Weihnachten? DEAL!";
                    break;
                case 4:
                    mess += "Ähm ich glaub zwar du hast da 'ne Null vergessen, aber okay!";
                    break;
                case 5:
                    mess += "Ich hab quasi schon angenommen, als du überlegt hast, mir das so günstig zu geben.";
                    break;
                default:
                    mess += "Also wenn ich das nicht annehmen würde, wäre ich kein Geschäftsmann!";
            }
        }
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().msgLocal(ki, mess);
        }
    }

    static void propertyMessage(Player ki, String name, int tradVal, int calcVal, int MINIMUM_ACCEPT_AMOUNT) {
        String mess = "";
        Random ran = new Random();
        int balance = tradVal - calcVal;
        int lowBoarder = 0 + MINIMUM_ACCEPT_AMOUNT;
        int highBoarder = 0 - MINIMUM_ACCEPT_AMOUNT;

        if (balance < lowBoarder) {         // zu teuer
            switch (ran.nextInt(3) + 1) {
                case 1:
                    mess += name + " zu diesem Preis? ziemlich unlukrativ";
                    break;
                case 2:
                    mess += "Also " + name + " schonmal nicht";
                    break;
                case 3:
                    mess += "Ehrlich? " + name + "?";
                    break;
            }
        }
        else if (balance < highBoarder) {  // gut
            switch (ran.nextInt(3) + 1) {
                case 1:
                    mess += name + " klingt ganz okay";
                    break;
                case 2:
                    mess += "Ja " + name + " passt ganz gut.";
                    break;
                case 3:
                    mess += "Hm, okay. " + name + "ist verhandelbar.";
                    break;
            }
        }
        else {                             // sehr gut
            switch (ran.nextInt(3) + 1) {
                case 1:
                    mess += name + " ist auf jeden Fall okay.";
                    break;
                case 2:
                    mess += "Wow " + name + "? Wenn du meinst.";
                    break;
                case 3:
                    mess += name + "? Na logo.";
                    break;
            }
        }
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().msgLocal(ki, mess);
        }
    }
}
