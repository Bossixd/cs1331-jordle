import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.robot.Robot;

/**
 * Keyboard class for the Jordle game.
 *
 * @author Pattakit Charoensedtakul
 * @version 1.0
 */
public class Keyboard extends VBox {
    private Robot r;
    private Color[] set;

    /**
     * Constructor for the Keyboard class.
     */
    public Keyboard() {
        r = new Robot();
        set = new Color[26];

        // QWERTY Keyboard layout
        String[] keys = {
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM"
        };

        // Add buttons for each row of keys
        for (int row = 0; row < keys.length; row++) {
            String rowKeys = keys[row];
            FlowPane keyboardRow = new FlowPane();
            keyboardRow.setAlignment(Pos.CENTER);

            // Add extra buttons for row 1 and 2 format
            if (row == 1) {
                Button button = new Button();
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(25, 40);
                keyboardRow.getChildren().add(button);
            } else if (row == 2) {
                Button button = new Button();
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(50, 40);
                keyboardRow.getChildren().add(button);
            }

            // Add buttons for each column of keys
            for (int col = 0; col < rowKeys.length(); col++) {
                char key = rowKeys.charAt(col);

                set[key - 'A'] = Color.WHITE;

                // Create a button for the key
                Button button = new Button(String.valueOf(key));
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(40, 40);
                keyboardRow.getChildren().add(button);

                button.setOnMouseEntered(e -> {
                    button.setStyle("-fx-background-color: lightgray;");
                });

                button.setOnMouseExited(e -> {
                    if (button.getText().length() != 1
                            || button.getText().charAt(0) - 'A' < 0
                            || button.getText().charAt(0) - 'A' > 25) {
                        System.out.println("Invalid key");
                        return;
                    }
                    Color keyColor = set[button.getText().charAt(0) - 'A'];
                    int red = ((int) Math.round(keyColor.getRed() * 255)) << 24;
                    int green = ((int) Math.round(keyColor.getGreen() * 255)) << 16;
                    int blue = ((int) Math.round(keyColor.getBlue() * 255)) << 8;
                    int opacity = ((int) Math.round(keyColor.getOpacity() * 255));
                    button.setStyle("-fx-background-color: " + String.format("#%08X", (red + green + blue + opacity)));
                });

                // Add event handling to print the key when clicked
                button.setOnAction(e -> {
                    r.keyPress(KeyCode.valueOf(String.valueOf(key).toUpperCase()));
                    r.keyRelease(KeyCode.valueOf(String.valueOf(key).toUpperCase()));
                });
            }

            // Add extra buttons for row 1 and 2 format
            if (row == 0) {
                Button button = new Button();
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(50, 40);
                keyboardRow.getChildren().add(button);
            } else if (row == 1) {
                Button button = new Button("ENTER");
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(65, 40);
                keyboardRow.getChildren().add(button);
                button.setOnAction(e -> {
                    r.keyPress(KeyCode.ENTER);
                    r.keyRelease(KeyCode.ENTER);
                });
            } else if (row == 2) {
                Button button = new Button();
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(55, 40);
                keyboardRow.getChildren().add(button);

                button = new Button("ERASE");
                button.getStyleClass().add("keyboard-button");
                button.setMinSize(65, 40);
                keyboardRow.getChildren().add(button);
                button.setOnAction(e -> {
                    r.keyPress(KeyCode.BACK_SPACE);
                    r.keyRelease(KeyCode.BACK_SPACE);
                });
            }

            // Add the row to the keyboard
            this.getChildren().add(keyboardRow);
        }
    }

    /**
     * Set the color of the given key in the keyboard. The key is case-insensitive.
     *
     * @param key the key to set the color for
     * @param color the color to set the key to
     */
    public void setColor(char key, Color color) {
        // Go through each row
        for (int row = 0; row < this.getChildren().size(); row++) {
            // Go through each button
            for (int i = 0; i < ((FlowPane) this.getChildren().get(row)).getChildren().size(); ++i) {
                // Get button
                Button button = (Button) ((FlowPane) this.getChildren().get(row)).getChildren().get(i);

                // Set key to uppercase
                if (key > 'Z') {
                    key += 'A' - 'a';
                }

                // Set button color
                if (button.getText().equals(String.valueOf(key))) {
                    int red = ((int) Math.round(color.getRed() * 255)) << 24;
                    int green = ((int) Math.round(color.getGreen() * 255)) << 16;
                    int blue = ((int) Math.round(color.getBlue() * 255)) << 8;
                    int opacity = ((int) Math.round(color.getOpacity() * 255));
                    button.setStyle("-fx-background-color: " + String.format("#%08X", (red + green + blue + opacity)));
                    set[button.getText().charAt(0) - 'A'] = color;
                }
            }
        }
    }

    /**
     * Resets the color of the keys in the keyboard to white.
     */
    public void resetColor() {
        String keys = "QWERTYUIOPASDFGHJKLZXCVBNM";
        for (char key : keys.toCharArray()) {
            setColor(key, Color.WHITE);
        }
    }

    /**
     * Gets the color of the given key in the keyboard. The key is case-insensitive.
     *
     * @param key the key to get the color for
     * @return the color of the key
     */
    public Color getColor(char key) {
        return set[key - 'a'];
    }
}