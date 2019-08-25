package it.unibz.inf.ontop.protege.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Set size of JOptionPane, so that the warning message is visible on the screen
 */

public final class OptionPaneUtils {

    private static int maxCharactersPerLineCount = 150;

    private OptionPaneUtils() {

    }

    public static JOptionPane getPrettyOptionPane() {

        class PrettyOptionPane extends JOptionPane {


            PrettyOptionPane() {


            }


            @Override
            public int getMaxCharactersPerLineCount() {

                return maxCharactersPerLineCount;

            }


        }

        return new PrettyOptionPane();
    }

    public static void showPrettyMessageDialog(Component parent, Object message, String title, int type) {

        class PrettyOptionPane extends JOptionPane {


            PrettyOptionPane() {


            }

            @Override
            public int getMaxCharactersPerLineCount() {

                return maxCharactersPerLineCount;

            }

        }

        JOptionPane narrowPane = new PrettyOptionPane();
        narrowPane.setMessage(message);
        narrowPane.setMessageType(type);
        JDialog errorDialog = narrowPane.createDialog(parent, title);
        errorDialog.setVisible(true);
    }



}
