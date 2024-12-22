import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import java.io.IOException;

/**
 * The main class for the Jordle game.
 *
 * @author Pattakit Charoensedtakul
 * @version 1.0
 */
public class Jordle extends Application {
    private Stage primaryStage;
    private Scene welcomeScene;
    private Scene gameScene;

    private String username;

    private Label welcomeLabel;

    private int width = 900;
    private int height = 650;

    private StackPane[][] jordleGrid;
    private int currentRow = 0;
    private int currentCol = 0;
    private boolean gameOver = false;

    private Label instructionTitle;

    private Backend backend;

    private Keyboard keyboard;

    /**
     * Initializes the game's UI and starts the game loop.
     *
     * @param stage the primary stage of the application
     * @throws IOException if an error occurs loading the game scene
     */
    @Override
    public void start(Stage stage) throws IOException {
        jordleGrid = new StackPane[6][5];
        backend = new Backend();

        // Initialize Jordle Grid
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 5; ++j) {
                jordleGrid[i][j] = new StackPane();
                jordleGrid[i][j].getStyleClass().add("jordle-grid-block");
            }
        }

        // Initialize Stage
        primaryStage = stage;

        // Initialize Scenes
        welcomeScene = createWelcomeScene();
        welcomeScene.getStylesheets().add("style.css");

        gameScene = createGameScene();
        gameScene.getStylesheets().add("style.css");

        // Start Game
        stage.setTitle("Jordle");
        stage.setScene(welcomeScene);
        stage.show();
    }

    /**
     * Creates the welcome scene for the Jordle game.
     *
     * @return a Scene containing the welcome elements
     */
    private Scene createWelcomeScene() {
        // Create Page Design
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().add("jordle-welcome-box");
        String imagePath = "jordleImage.jpg";
        vBox.setStyle(String.format("-fx-background-image: url(%s);", imagePath));

        // Jordle Title
        Label jordleLabel = new Label("Jordle");
        jordleLabel.getStyleClass().add("jordle-welcome-text");

        // Play Button
        Button playButton = new Button("Play (Enter)");
        playButton.getStyleClass().add("play-button");
        vBox.getChildren().addAll(jordleLabel, playButton);

        // Play Button Action
        // Set scene to game scene
        playButton.setOnAction(e -> {
            primaryStage.setScene(gameScene);
            primaryStage.show();
        });

        // Instructions Button
        Scene welcomeSceneReturn = new Scene(vBox, width, height);

        // Instructions Button Action
        welcomeSceneReturn.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                primaryStage.setScene(gameScene);
                primaryStage.show();
            }
        });

        return welcomeSceneReturn;
    }

    /**
     * Creates the game scene for the Jordle game.
     *
     * @return a Scene containing the game elements
     */
    private Scene createGameScene() {
        VBox vBox = createGameSceneUI();

        // Create Scene
        Scene scene = new Scene(vBox, width, height);

        // Check for User Inputs
        // Lambda Expression!!!
        scene.setOnKeyPressed((KeyEvent event) -> keyPressAction(event));

        // Return Scene
        return scene;
    }

    /**
     * Creates the UI elements for the game scene of the Jordle game.
     *
     * @return a VBox containing all the UI elements for the game scene
     */
    private VBox createGameSceneUI() {
        // Initialize Backend
        backend = new Backend();

        // Create Page Design
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        // Jordle Title
        Label jordleTitle = new Label("JORDLE");
        jordleTitle.getStyleClass().add("jordle-title");

        // Instruction Title
        instructionTitle = new Label("Guess a word!");
        instructionTitle.getStyleClass().add("jordle-instruction");

        // Initialize Jordle Grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Create Jordle Grid
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 6; ++j) {
                Rectangle rect = new Rectangle();
                rect.setWidth(50);
                rect.setHeight(50);
                rect.setFill(Color.WHITE);
                rect.setStroke(Color.BLACK);

                Label label = new Label();

                jordleGrid[j][i].getChildren().addAll(rect, label);
                grid.add(jordleGrid[j][i], i, j);
            }
        }

        // User Buttons HBox
        HBox userButtons = new HBox();
        userButtons.setAlignment(Pos.CENTER);

        // Create User Buttons
        Button restartButton = new Button("Restart (1)");
        restartButton.getStyleClass().add("user-button");

        // Annonymous Inner Class!!!
        restartButton.setOnAction(
            new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    restartGame();
                }
            }
        );

        Button instructionsButton = new Button("Instructions (2)");
        instructionsButton.getStyleClass().add("user-button");

        // Annonymous Inner Class!!!
        instructionsButton.setOnAction(
            new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    openInstructionPage();
                }
            }
        );

        // Add User Buttons to HBox
        userButtons.getChildren().addAll(restartButton, instructionsButton);

        // Create Keyboard
        // VBox keyboard = createKeyboard();
        keyboard = new Keyboard();
        keyboard.setAlignment(Pos.CENTER);

        // Add Elements to VBox
        vBox.getChildren().addAll(jordleTitle, instructionTitle, grid, userButtons, keyboard);

        return vBox;
    }

    /**
     * Resets the game state to start a new game round.
     * Resets keyboard colors, clears the Jordle grid, and sets the game over flag to false.
     */
    private void restartGame() {
        backend.reset();
        currentRow = 0;
        currentCol = 0;
        gameOver = false;
        instructionTitle.setText("Guess a word!");
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 5; ++j) {
                ((Rectangle) jordleGrid[i][j].getChildren().get(0)).setFill(Color.WHITE);
                ((Label) jordleGrid[i][j].getChildren().get(1)).setText("");
            }
        }
        keyboard.resetColor();
    }

    /**
     * Opens a new stage with instructions for the game.
     */
    private void openInstructionPage() {
        Stage instructionsStage = new Stage();
        instructionsStage.setTitle("Instructions");
        VBox instructionsVBox = new VBox();
        instructionsVBox.setAlignment(Pos.CENTER);
        Label instructionsLabel = new Label("Try to guess the 5-letter word!\n"
                                            + "Green means the character is in the right spot.\n"
                                            + "Yellow means the character is in the wrong spot.\n"
                                            + "Gray means the character is not in the word.\n"
                                            + "Press 1 to restart the game. Press 2 to open instructions.\n"
                                            + "Press enter to submit your guess. Press escape to exit this page.");
        instructionsVBox.getChildren().add(instructionsLabel);
        Scene instructionScene = new Scene(instructionsVBox, 375, 125);
        instructionsStage.setScene(instructionScene);
        instructionsStage.show();

        // Close instructions stage when escape is pressed
        instructionScene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                instructionsStage.close();
            }
        });
    }

    /**
     * Displays an alert message for an invalid word input.
     */
    public void openInvalidAlert() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Invalid Word!");
        a.setHeaderText("Input a word with 5 letters!");
        a.showAndWait();
    }


    /**
     * Deals with user keyboard inputs.
     *
     * @param event The key event
     */
    public void keyPressAction(KeyEvent event) {
        String input = event.getText();

        // If 1 or 2 is pressed, restart game or open instruction page
        if (!input.isEmpty()) {
            if (input.charAt(0) == '1') {
                restartGame();
                return;
            } else if (input.charAt(0) == '2') {
                openInstructionPage();
                return;
            }
        }

        // If Game Over, do nothing
        if (gameOver) {
            return;
        }

        // If Enter is Pressed, Check Guess and Determine if Game Over
        if (event.getCode() == KeyCode.ENTER) {
            // If currentCol is not 5, do nothing
            if (currentCol != 5) {
                openInvalidAlert();
            }

            // Create Guess from Jordle Grid
            String guess = "";
            for (int i = 0; i < 5; ++i) {
                guess += ((Label) jordleGrid[currentRow][i].getChildren().get(1)).getText();
            }

            String result;

            // Check if guess is valid
            try {
                result = backend.check(guess);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }

            // Update Jordle Grid
            int correctGuesses = 0;
            for (int i = 0; i < 5; ++i) {
                // Get Rectangle and Label
                Rectangle rect = (Rectangle) jordleGrid[currentRow][i].getChildren().get(0);
                Label label = (Label) jordleGrid[currentRow][i].getChildren().get(1);
                char character = label.getText().charAt(0);

                // Create Timeline
                Timeline timeline = new Timeline();
                timeline.setCycleCount(2);
                timeline.setAutoReverse(true);

                if (result.charAt(i) == 'g') {
                    keyboard.setColor(character, Color.LIGHTGREEN);
                    correctGuesses += 1;
                    rect.setFill(Color.LIGHTGREEN);

                    KeyValue keyValueX = new KeyValue(rect.scaleXProperty(), 1.05);
                    KeyValue keyValueY = new KeyValue(rect.scaleYProperty(), 1.05);
                    KeyValue keyValueRotate = new KeyValue(rect.rotateProperty(), 10);
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(150), keyValueX, keyValueY, keyValueRotate);

                    timeline.getKeyFrames().add(keyFrame);

                } else if (result.charAt(i) == 'y') {
                    rect.setFill(Color.YELLOW);

                    KeyValue keyValueX = new KeyValue(rect.scaleXProperty(), 1.1);
                    KeyValue keyValueY = new KeyValue(rect.scaleYProperty(), 1.1);
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(150), keyValueX, keyValueY);

                    timeline.getKeyFrames().add(keyFrame);

                    if (keyboard.getColor(character) != Color.LIGHTGREEN) {
                        keyboard.setColor(character, Color.YELLOW);
                    }
                } else if (result.charAt(i) == 'i') {
                    rect.setFill(Color.GRAY);

                    if (keyboard.getColor(character) != Color.LIGHTGREEN
                            && keyboard.getColor(character) != Color.YELLOW) {
                        keyboard.setColor(character, Color.GRAY);
                    }
                }
                timeline.play();
            }

            // Update Row and Column
            currentRow += 1;
            currentCol = 0;

            // If Correct Guesses == 5, Game Over, You Win!
            if (correctGuesses == 5) {
                instructionTitle.setText("You win!");
                gameOver = true;
                return;
            }

            // If Current Row == 6, Game Over, You Lose!
            if (currentRow == 6) {
                instructionTitle.setText("You lose! The word was: " + backend.getTarget());
                gameOver = true;
                return;
            }

            return;
        }

        // If Backspace is Pressed, Delete Last Letter
        if (event.getCode() == KeyCode.BACK_SPACE) {
            if (currentCol == 0) {
                return;
            }
            currentCol -= 1;
            ((Label) jordleGrid[currentRow][currentCol].getChildren().get(1)).setText("");
            return;
        }

        // If Shift is Pressed, Do Nothing
        if (event.getCode() == KeyCode.SHIFT) {
            return;
        }

        // If Input is Empty, Do Nothing
        if (input.isEmpty()) {
            return;
        }

        // If Input is Not a Letter, Do Nothing
        if (!input.isEmpty()
                && !((int) input.charAt(0) >= (int) 'A'
                        && (int) input.charAt(0) <= (int) 'Z')
                && !((int) input.charAt(0) >= (int) 'a'
                        && (int) input.charAt(0) <= (int) 'z')) {
            return;
        }

        // If Current Col == 5, Do Nothing
        if (currentCol == 5) {
            return;
        }

        // Add Input to Jordle Grid
        ((Label) jordleGrid[currentRow][currentCol].getChildren().get(1)).setText(input.toLowerCase());
        currentCol += 1;
    }
}