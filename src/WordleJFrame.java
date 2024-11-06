package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleJFrame extends JFrame {
    // Array som innehåller tillgängliga ord för spelet.
    private static final String[] WORD_ARRAY = {"TEST", "READ", "ROAD", "POLE", "DOWN", "QUIT", "BLUE", "COOL", "BEER", "DART", "JAVA", "PLAY", "GAME", "WORD", "TYPE", "DONE", "HARD", "EASY"};
    private String dailyWord;  // Slumpmässigt ord för spelet.
    private final List<JTextField> guessTextFields = new ArrayList<>(); // Lista över textfält där spelaren skriver sin gissning.
    private int remainingTries = 10;  // Antal försök spelaren har kvar.
    private String inputText = "";  // Håller nuvarande gissning.

    private JPanel gameArea;  // Huvudpanelen för spelet.

    public WordleJFrame() {
        // Väljer ett slumpmässigt ord från ordlistan som dagens ord.
        dailyWord = WORD_ARRAY[new Random().nextInt(WORD_ARRAY.length)]; 
        System.out.println("Today's word is: " + dailyWord); // Visar ordet i konsolen (bra för testing men borde tas bort i en produktionsversion.).

        // Initialiserar spelets gränssnitt.
        setupGameArea();

        // Lägg till huvudpanelen till JFrame och ställ in standardinställningar.
        this.add(gameArea); 
        this.setBounds(200, 200, 600, 600); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setVisible(true); 
    }

    // Skapar och konfigurerar spelets gränssnitt.
    private void setupGameArea() {
        gameArea = new JPanel(); 
        gameArea.setBounds(200, 200, 520, 200);
        gameArea.setLayout(new BoxLayout(gameArea, BoxLayout.LINE_AXIS));

        JLabel titleLabel = new JLabel("Wordle"); // Titel för spelet.
        gameArea.add(titleLabel);

        // Skapar fyra textfält för spelarens gissning och lägger till dem i huvudpanelen.
        for (int i = 0; i < 4; i++) {
            JTextField letterField = createLetterInputField(); 
            guessTextFields.add(letterField); 
            gameArea.add(letterField); 
        }

        // Skapar och lägger till en knapp för att skicka in gissningen.
        JButton submitButton = createSubmitButton();
        gameArea.add(submitButton);
    }

    // Skapar textfält för varje bokstav som ska gissas.
    private JTextField createLetterInputField() {
        JTextField letterField = new JTextField("", 1);
        letterField.setHorizontalAlignment(JTextField.CENTER);
        letterField.setFont(new Font(Font.SERIF, Font.BOLD, 50));
        letterField.setAlignmentX(0.5f);
        
        // Lägg till en tangentbordslyssnare för att hantera inmatning och navigering mellan fält.
        letterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleNavigationKeys(e, letterField);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleInputCharacter(e, letterField);
            }
        });

        return letterField;
    }

    // Hanterar tangentbordsnavigering (vänster och höger pil) mellan textfälten.
    private void handleNavigationKeys(KeyEvent e, JTextField field) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT && guessTextFields.indexOf(field) > 0) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
            e.consume();
        } else if (keyCode == KeyEvent.VK_RIGHT && guessTextFields.indexOf(field) < guessTextFields.size() - 1) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            e.consume();
        } else if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CAPS_LOCK) {
            e.consume();
        }
    }

    // Hanterar teckeninmatning så att endast bokstäver tillåts.
    private void handleInputCharacter(KeyEvent e, JTextField field) {
        char c = e.getKeyChar();
        if (Character.isLetter(c)) {
            field.setText(String.valueOf(c).toUpperCase());
        } else {
            field.setText("");
        }
    }

    // Skapar en knapp för att skicka in spelarens gissning.
    private JButton createSubmitButton() {
        JButton submitButton = new JButton("Submit Word");
        submitButton.addActionListener(e -> submitWord());
        return submitButton;
    }

    // Samlar spelarens gissning och kontrollerar om den är korrekt.
    private void submitWord() {
        inputText = "";
        for (JTextField field : guessTextFields) {
            inputText += field.getText();
        }

        if (inputText.length() == 4) {
            remainingTries--;
            System.out.println("Your guess: " + inputText);
            provideFeedback();
        } else {
            System.out.println("Please enter all 4 letters!");
        }
    }

    // Ger feedback till spelaren efter varje gissning.
    private void provideFeedback() {
        if (dailyWord.equals(inputText)) {
            System.out.println("Congratulations! You've guessed the word!");
            resetGame();  // Återställer spelet om gissningen är korrekt.
        } else {
            // Går igenom varje bokstav för att kontrollera om den är korrekt.
            for (int i = 0; i < 4; i++) {
                char guessedLetter = inputText.charAt(i);
                if (dailyWord.charAt(i) == guessedLetter) {
                    System.out.println("Letter " + guessedLetter + " is in the correct position.");
                } else if (dailyWord.contains(String.valueOf(guessedLetter))) {
                    System.out.println("Letter " + guessedLetter + " is in the word but wrong position.");
                } else {
                    System.out.println("Letter " + guessedLetter + " is not in the word.");
                }
            }
            // Om spelaren har slut på försök, visas rätt ord och spelet återställs.
            if (remainingTries == 0) {
                System.out.println("Game over! The word was: " + dailyWord);
                resetGame();
            } else {
                System.out.println("Tries left: " + remainingTries);
            }
        }
    }

    // Återställer spelet för att låta spelaren börja om.
    private void resetGame() {
        remainingTries = 10;
        inputText = "";
        dailyWord = WORD_ARRAY[new Random().nextInt(WORD_ARRAY.length)];
        guessTextFields.forEach(field -> field.setText(""));
    }
}
