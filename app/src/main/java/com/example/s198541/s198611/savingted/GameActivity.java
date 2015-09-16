package com.example.s198541.s198611.savingted;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements EndGameDialog.DialogClickListener,
        EndSessionDialog.DialogClickListener, NewGameCategoryDialog.DialogClickListener, ResetWarningDialog.DialogClickListener {

    private static final int GUESS_WORD_TEXT_SIZE = 20;
    private static final int GUESS_WORD_PADDING = 8;
    private static final int NEW_LINE_KEYBOARD_FIRST = 10;
    private static final int NEW_LINE_KEYBOARD_SECOND = 20;
    private static final int NEW_LINE_KEYBOARD_FIRST_LAND = 15;
    private static final int KEYBOARD_TEXT_SIZE = 16;
    private static final int KEYBOARD_MARGIN = 2;
    private static final int KEYBOARD_WIDTH = 38;
    private static final int KEYBOARD_HEIGHT = 55;
    private static final int KEYBOARD_WIDTH_LAND = 44;
    private static final int KEYBOARD_HEIGHT_LAND = 33;
    private static final int BUTTON_ID_START = 50;
    private static final int[] IMAGE_IDS = {R.drawable.hangman_1, R.drawable.hangman_2, R.drawable.hangman_3,
            R.drawable.hangman_4, R.drawable.hangman_5, R.drawable.hangman_6, R.drawable.hangman_siste};

    private Resources res;
    private String[] words;
    private int wordCounter = 0;
    private String currentWord;
    private String[] alphabetLetters;
    private int numLettersGuessed = 0;
    private int imageCounter = 0;
    private int gamesWon;
    private int gamesTotal;
    private int chosenCategoryIndex;

    // Store in SharedPreferences:
    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("gamesWon", gamesWon + "").putString("gamesTotal", gamesTotal + "")
                .commit();
    }

    // Get values from SharedPreferences:
    @Override
    protected void onResume() {
        super.onResume();

        String gamesWonString = (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("gamesWon", ""));
        String gamesTotalString = (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("gamesTotal", ""));

        try {
            gamesWon = Integer.parseInt(gamesWonString);
            gamesTotal = Integer.parseInt(gamesTotalString);
        } catch (NumberFormatException nfe) {
            gamesWon = 0;
            gamesTotal = 0;
        }

        updateGamesWonTextView();
    }

    // Store values
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Storing the private attributes:
        outState.putStringArray("WORDS", words);
        outState.putInt("WORD_COUNTER", wordCounter);
        outState.putString("CURRENT_WORD", currentWord);
        outState.putInt("NUM_LETTERS_GUESSED", numLettersGuessed);
        outState.putInt("IMAGE_COUNTER", imageCounter);
        outState.putInt("GAMES_WON", gamesWon);
        outState.putInt("GAMES_TOTAL", gamesTotal);
        outState.putInt("CHOSEN_CATEGORY_INDEX", chosenCategoryIndex);

        // Storing the text-color to each button in the keyboard:
        int[] buttonColorArray = new int[alphabetLetters.length];
        addColorsToArray(buttonColorArray);
        outState.putIntArray("BUTTON_COLORS", buttonColorArray);

        // Storing how many/which letters have been guessed:
        char[] currentGuessArea = new char[currentWord.length()];
        addCharactersToArray(currentGuessArea);
        outState.putCharArray("CURRENT_GUESS_AREA", currentGuessArea);

        super.onSaveInstanceState(outState);
    }

    // Called from onSaveInstanceState()
    public void addColorsToArray(int[] buttonColorArray) {
        for (int i = 0; i < alphabetLetters.length; i++) {
            Button keyboardButton = (Button) findViewById(i + BUTTON_ID_START);  // find each button
            int color = keyboardButton.getCurrentTextColor();
            buttonColorArray[i] = color;
        }
    }

    // Called from onSaveInstanceState()
    public void addCharactersToArray(char[] currentGuessArea) {
        for (int i = 0; i < currentWord.length(); i++) {
            TextView guessAreaLetter = (TextView) findViewById(i);  // find each TextView in the guess-area
            currentGuessArea[i] = guessAreaLetter.getText().charAt(0);  // get the letter
        }
    }

    // Get stored values
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Getting the stored private attributes:
        words = savedInstanceState.getStringArray("WORDS");
        wordCounter = savedInstanceState.getInt("WORD_COUNTER");
        currentWord = savedInstanceState.getString("CURRENT_WORD");
        numLettersGuessed = savedInstanceState.getInt("NUM_LETTERS_GUESSED");
        imageCounter = savedInstanceState.getInt("IMAGE_COUNTER");
        gamesWon = savedInstanceState.getInt("GAMES_WON");
        gamesTotal = savedInstanceState.getInt("GAMES_TOTAL");
        chosenCategoryIndex = savedInstanceState.getInt("CHOSEN_CATEGORY_INDEX");

        // Restore image:
        showLastImage();

        // Restore guessWordArea:
        createGuessWordArea(currentWord);
        char[] currentGuessArea = savedInstanceState.getCharArray("CURRENT_GUESS_AREA");
        updateGuessWordArea(currentGuessArea);

        // Restore keyboard:
        createKeyboard();
        int[] buttonColorArray = savedInstanceState.getIntArray("BUTTON_COLORS");
        updateKeyboard(buttonColorArray);
    }

    // Called from onRestoreInstanceState()
    public void updateGuessWordArea(char[] currentGuessArea) {
        for(int i = 0; i < currentGuessArea.length; i++) {
            TextView guessAreaLetter = (TextView) findViewById(i);

            char letter = currentGuessArea[i];

            if(letter != '_')   // not necessary to change the text if it is changed to what it was in the first place
                guessAreaLetter.setText(letter + "");
        }
    }

    // Called from onRestoreInstanceState()
    public void updateKeyboard(int[] buttonColorArray) {
        for (int i = 0; i < buttonColorArray.length; i++) {
            Button button = (Button) findViewById(i + BUTTON_ID_START);

            int color = buttonColorArray[i];

            if (color == Color.GREEN || color == Color.RED) {    // then the button has been clicked before
                button.setTextColor(color);
                button.setEnabled(false);
            }
        }
    }

    // Called from onRestoreInstanceState()
    public void showLastImage() {
        if (res.getConfiguration().orientation == 1) // then it is ORIENTATION_PORTRAIT (image set to ImageView)
        {
            // show the image in the ImageView main_image:
            ImageView imageView = (ImageView) findViewById(R.id.main_image);

            if (imageCounter == 0) { // the array IMAGE_IDS, that the imageCounter counts on, does not contain the first image
                imageView.setBackgroundResource(R.drawable.hangman_forste);
            } else {
                imageView.setBackgroundResource(IMAGE_IDS[imageCounter - 1]); // because of imageCounter++ when an image is set
            }
        } else {    // then it is ORIENTATION_LANDSCAPE (2) (image set to LinearLayout)
            // show the image in the LinearLayout image_layout_land:
            LinearLayout layout = (LinearLayout) findViewById(R.id.image_layout_land);

            if (imageCounter == 0) { // the array IMAGE_IDS, that the imageCounter counts on, does not contain the first image
                layout.setBackgroundResource(R.drawable.hangman_forste);
            } else {
                layout.setBackgroundResource(IMAGE_IDS[imageCounter - 1]); // because of imageCounter++ when an image is set
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        res = getResources();
        alphabetLetters = res.getStringArray(R.array.listAlphabet);

        // If there is no saved instance yet:
        if (savedInstanceState == null) { // Then: create pop-up to choose category:
            String[] categories = res.getStringArray(R.array.listCategories);

            // Pop-up-dialogs where the player chooses category:
            String catTitle = getString(R.string.choose_category);
            NewGameCategoryDialog catDialog = NewGameCategoryDialog.newInstance(catTitle, categories);
            catDialog.show(getFragmentManager(), "TAG");
            // Waiting for the player to make a choice
        }
    }

    // NewGameCategoryDialog-method:
    @Override
    public void onItemClick(int chosenItemIndex) {
        chosenCategoryIndex = chosenItemIndex;
        setWordsFromCategoryChoice();

        currentWord = getNextWord();

        if (currentWord != null) {
            createGuessWordArea(currentWord);
            createKeyboard();
        } else {
            endOfSessionDialog();
        }
    }

    // NewGameCategoryDialog-method:
    @Override
    public void onCancelClick() {
        finish();
    }

    // EndGameDialog-method:
    @Override
    public void onOkClick() {
        currentWord = getNextWord();

        if (currentWord != null) {
            resetValues();
            createGuessWordArea(currentWord);
        } else {
            endOfSessionDialog();
        }
    }

    // EndSessionDialog-method:
    @Override
    public void onNewGameClick() {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
        finish();
    }

    // EndSessionDialog-method:
    @Override
    public void onQuitGameClick() {
        finish();
    }

    // ResetWarningDialog-method:
    @Override
    public void onResetClick() {
        gamesWon = 0;
        gamesTotal = 0;
        updateGamesWonTextView();
    }

    // ResetWarningDialog-method:
    @Override
    public void onCancelResetClick() {
        // do nothing
    }

    // Reset values-button clicked: Reset gamesWon and gamesTotal
    public void resetButtonClicked(View view) {
        // Pop-up with warning first:
        String title = getString(R.string.reset_warning_title);
        String message = getString(R.string.reset_warning_message);

        ResetWarningDialog dialog = ResetWarningDialog.newInstance(title, message);
        dialog.show(getFragmentManager(), "TAG");
        // Waiting for the user to make a choice
    }

    // The user has chosen a category from the pop-up dialog NewGameCategoryDialog
    public void setWordsFromCategoryChoice() {
        // reading words from the right category from xml-file (arrays.xml):
        switch (chosenCategoryIndex) {
            case 0:
                words = res.getStringArray(R.array.listWordsAtTheStore);
                break;
            case 1:
                words = res.getStringArray(R.array.listWordsOutside);
                break;
            case 2:
                words = res.getStringArray(R.array.listWordsAnimals);
                break;
            default:
                String[] atTheStoreWords = res.getStringArray(R.array.listWordsAtTheStore);
                String[] outsideWords = res.getStringArray(R.array.listWordsOutside);
                String[] animalsWords = res.getStringArray(R.array.listWordsAnimals);

                List<String> allWords = new ArrayList<>();

                allWords.addAll(Arrays.asList(atTheStoreWords));
                allWords.addAll(Arrays.asList(outsideWords));
                allWords.addAll(Arrays.asList(animalsWords));

                words = allWords.toArray(new String[allWords.size()]);
                break;
        }

        // shuffle words (random which word to guess first):
        List<String> shuffledList = Arrays.asList(words);
        Collections.shuffle(shuffledList);
        words = shuffledList.toArray(new String[shuffledList.size()]);
    }

    public String getNextWord() {
        if (wordCounter >= words.length)
            return null;

        return words[wordCounter++];
    }

    public void updateGamesWonTextView() {
        TextView gamesWonTextView = (TextView) findViewById(R.id.games_won_textview);
        gamesWonTextView.setText(gamesWon + "/" + gamesTotal);
    }

    public void createGuessWordArea(String chosenWord) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.guessing_word_layout);
        layout.removeAllViews();    // if there is a word here from before the TextViews are removed

        for (int i = 0; i < chosenWord.length(); i++) {
            TextView textViewLetter = new TextView(this);
            textViewLetter.setTextSize(GUESS_WORD_TEXT_SIZE);
            textViewLetter.setText("_");
            textViewLetter.setId(i);
            textViewLetter.setPadding(GUESS_WORD_PADDING, 0, GUESS_WORD_PADDING, 0);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewLetter.setLayoutParams(layoutParams);
            layout.addView(textViewLetter);
        }
    }

    public void createKeyboard() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_1); // place button/letter on row 1

        // Going through all the letters and creating a button for each letter:
        for (int i = 0; i < alphabetLetters.length; i++) {

            if (res.getConfiguration().orientation == 1) { // then it is ORIENTATION_PORTRAIT
                if (i >= NEW_LINE_KEYBOARD_FIRST && i < NEW_LINE_KEYBOARD_SECOND) // then place button/letter on row 2
                    layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_2);
                else if (i >= NEW_LINE_KEYBOARD_SECOND) // then place button/letter on row 3
                    layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_3);
            } else { // then it is ORIENTATION_LANDSCAPE (2)
                if (i >= NEW_LINE_KEYBOARD_FIRST_LAND) // then place button/letter on row 2
                    layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_2);
            }

            // Creating a button (one button for each letter) for the keyboard and adding it to the specified layout:
            Button buttonLetter = createButton(i);
            buttonLetter.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Button b = (Button) view;
                    char guessedLetter = b.getText().charAt(0);

                    b.setEnabled(false); // disabling onclick on the button

                    if (correctGuessedLetter(guessedLetter)) {  // then change color for the letter to green:
                        b.setTextColor(Color.GREEN);

                        if (numLettersGuessed >= currentWord.length())  // the word is guessed - the player won
                            endOfGameDialog(true);
                    } else {    // then change color for the letter to red:
                        b.setTextColor(Color.RED);

                        showNextImage();

                        // If this was the last image: weren't able to guess the word
                        // If now, after imageCounter++ (set in showNextImage()), imageCounter is the
                        // same as the length of the image-array, the last image has just been shown,
                        // and the player weren't able to guess the word
                        if (imageCounter >= IMAGE_IDS.length)
                            endOfGameDialog(false);
                    }
                }
            });

            setLayoutParams(buttonLetter);
            layout.addView(buttonLetter);
        }
    }

    // Called from createKeyboard()
    public Button createButton(int i) {
        Button buttonLetter = new Button(this);
        buttonLetter.setId(i + BUTTON_ID_START);
        buttonLetter.setTextColor(Color.WHITE);
        buttonLetter.setTextSize(KEYBOARD_TEXT_SIZE);
        buttonLetter.setText(alphabetLetters[i]);
        buttonLetter.setBackgroundResource(R.drawable.custom_keyboard_button);
        return buttonLetter;
    }

    // Called from createKeyboard() inside onClick() to Button
    public void showNextImage() {
        if (res.getConfiguration().orientation == 1) // then it is ORIENTATION_PORTRAIT
        {
            // show next image in the ImageView main_image:
            ImageView imageView = (ImageView) findViewById(R.id.main_image);
            imageView.setBackgroundResource(IMAGE_IDS[imageCounter++]);
        } else {    // then it is ORIENTATION_LANDSCAPE (2)
            // show next image in the LinearLayout image_layout_land:
            LinearLayout layout = (LinearLayout) findViewById(R.id.image_layout_land);
            layout.setBackgroundResource(IMAGE_IDS[imageCounter++]);
        }
    }

    // Called from createKeyboard()
    public void setLayoutParams(Button buttonLetter) {
        LinearLayout.LayoutParams layoutParams;

        // The keyboard-buttons in portrait and landscape are going to have different dimensions:
        if (res.getConfiguration().orientation == 1) { // then it is ORIENTATION_PORTRAIT
            layoutParams = new LinearLayout.LayoutParams(KEYBOARD_WIDTH, KEYBOARD_HEIGHT);
        } else {    // then it is ORIENTATION_LANDSCAPE
            layoutParams = new LinearLayout.LayoutParams(KEYBOARD_WIDTH_LAND, KEYBOARD_HEIGHT_LAND);
        }

        layoutParams.setMargins(KEYBOARD_MARGIN, KEYBOARD_MARGIN, KEYBOARD_MARGIN, KEYBOARD_MARGIN);
        buttonLetter.setLayoutParams(layoutParams);
    }

    public void clearKeyboard() {
        LinearLayout layoutRow1 = (LinearLayout) findViewById(R.id.keyboard_layout_row_1);
        layoutRow1.removeAllViews();

        LinearLayout layoutRow2 = (LinearLayout) findViewById(R.id.keyboard_layout_row_2);
        layoutRow2.removeAllViews();

        if(res.getConfiguration().orientation == 1) { // then it is ORIENTATION_PORTRAIT and portrait has 3 keyboard-rows
            LinearLayout layoutRow3 = (LinearLayout) findViewById(R.id.keyboard_layout_row_3);
            layoutRow3.removeAllViews();
        }
    }

    public boolean correctGuessedLetter(char letter) {
        boolean correct = false;

        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == letter) {
                TextView textViewFoundLetter = (TextView) findViewById(i);
                textViewFoundLetter.setText(letter + "");
                numLettersGuessed++;
                correct = true;
            }
        }

        return correct;
    }

    public void endOfGameDialog(boolean wordGuessed) {
        String title;
        String message;

        if (wordGuessed) {  // the player guessed the word
            title = getString(R.string.word_guessed_title);
            message = getString(R.string.word_guessed);
            gamesWon++;
        } else {            // the player did not guess the word
            title = getString(R.string.word_not_guessed_title);
            message = getString(R.string.word_was) + " " + currentWord + ". ";
            message += getString(R.string.word_not_guessed);
        }

        gamesTotal++;
        updateGamesWonTextView();

        // Creating pop-up/dialog with title and message that has an OK-button:
        EndGameDialog dialog = EndGameDialog.newInstance(title, message);
        dialog.show(getFragmentManager(), "TAG");
        // When the user clicks OK (the only button), the method onOkClick() is called
    }

    // Called after a game has been won or lost (the player guessed the word or not)
    // Called after the player has clicked the OK-button for the dialog/pop-up
    public void resetValues() {
        numLettersGuessed = 0;
        imageCounter = 0;

        // reset image:
        if (res.getConfiguration().orientation == 1) { // then it is ORIENTATION_PORTRAIT)
            ImageView imageView = (ImageView) findViewById(R.id.main_image);
            imageView.setBackgroundResource(R.drawable.hangman_forste);
        } else { // then it is ORIENTATION_LANDSCAPE (2)
            LinearLayout layout = (LinearLayout) findViewById(R.id.image_layout_land);
            layout.setBackgroundResource(R.drawable.hangman_forste);
        }

        clearKeyboard();
        createKeyboard();
    }

    // Called when all the words have been used
    public void endOfSessionDialog() {
        String title = getString(R.string.end_of_session_title);
        String message = getString(R.string.end_of_session);

        // Creating pop-up/dialog with title and message that has a NEW GAME-button and a QUIT-button:
        EndSessionDialog dialog = EndSessionDialog.newInstance(title, message);
        dialog.show(getFragmentManager(), "TAG");
        // When the user clicks NEW GAME the method onNewGameClick() is called
        // When the user click QUIT the method onQuitGameClick() is called
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_front_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.new_game:
                Intent i = new Intent(this, GameActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.rules:
                Intent i2 = new Intent(this, RulesActivity.class);
                startActivity(i2);
                finish();
                return true;
            case R.id.settings:
                Intent i3 = new Intent(this, SettingsActivity.class);
                startActivity(i3);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
