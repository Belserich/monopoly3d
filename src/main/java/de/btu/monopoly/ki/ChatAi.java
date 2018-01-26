/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.service.IOService;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class ChatAi {

    private static final Logger LOGGER = Logger.getLogger(HardKi.class.getCanonicalName());

    public static void sendChatMessage(String chatter, String message, boolean isLocal) {
        //TODO
        LOGGER.info(chatter + ": " + message);
    }

    static void tradeResultMessage(String ki, int balance, int MINIMUM_ACCEPT_AMOUNT) {
        Random ran = new Random();
        String mess = "";

        if (balance < -(MINIMUM_ACCEPT_AMOUNT * 6)) {           // viel zu teuer
            switch (ran.nextInt(8)) {
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
            switch (ran.nextInt(6)) {
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
        else if (balance < -(MINIMUM_ACCEPT_AMOUNT * 6)) {      // okay
            switch (ran.nextInt(6)) {
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
            switch (ran.nextInt(6)) {
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
        sendChatMessage(ki, mess, true);
    }

    static void propertyMessage(int balance) {
        // TODO
    }

}
