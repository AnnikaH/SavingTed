package com.example.s198541.s198611.savingted;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements EndGameDialog.DialogClickListener,
        EndSessionDialog.DialogClickListener, NewGameDialog.DialogClickListener, ResetWarningDialog.DialogClickListener {

    // TODO: Many of these values can be elements in dimens.xml and we can get to them via R.dimen.name ?
    private static final int GUESS_WORD_TEXT_SIZE = 20;
    private static final int GUESS_WORD_PADDING = 8;
    private static final int NEW_LINE_KEYBOARD_FIRST = 10;
    private static final int NEW_LINE_KEYBOARD_SECOND = 20;
    private static final int KEYBOARD_TEXT_SIZE = 16;
    private static final int KEYBOARD_MARGIN = 2;
    private static final int KEYBOARD_WIDTH = 38;
    private static final int KEYBOARD_HEIGHT = 55;

    private static final int[] IMAGE_IDS = {R.drawable.hangman_1, R.drawable.hangman_2,
            R.drawable.hangman_3, R.drawable.hangman_4, R.drawable.hangman_5, R.drawable.hangman_6,
            R.drawable.hangman_siste};

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

    // TODO: FINISH THIS
    // Store values
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Storing the private attributes:
        outState.putStringArray("WORDS", words);
        outState.putInt("WORD_COUNTER", wordCounter);
        outState.putString("CURRENT_WORD", currentWord);
        //outState.putStringArray("ALPHABET_LETTERS", alphabetLetters);
        outState.putInt("NUM_LETTERS_GUESSED", numLettersGuessed);
        outState.putInt("IMAGE_COUNTER", imageCounter);
        outState.putInt("GAMES_WON", gamesWon);
        outState.putInt("GAMES_TOTAL", gamesTotal);
        outState.putInt("CHOSEN_CATEGORY_INDEX", chosenCategoryIndex);

        /* TODO: Hva man må vite:
            Hvilke bokstaver som er funnet i ordet man gjetter på
            Hvordan keyboardet ser ut (bokstaver røde el. grønne el. hvite)
            Hvilket bilde man har kommet til (imageCounter nok?)
        */

        super.onSaveInstanceState(outState);
    }

    // TODO: FINISH THIS
    // Get stored values
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Getting the stored private attributes:
        words = savedInstanceState.getStringArray("WORDS");
        wordCounter = savedInstanceState.getInt("WORD_COUNTER");
        currentWord = savedInstanceState.getString("CURRENT_WORD");
        //alphabetLetters = savedInstanceState.getStringArray("ALPHABET_LETTERS");
        numLettersGuessed = savedInstanceState.getInt("NUM_LETTERS_GUESSED");
        imageCounter = savedInstanceState.getInt("IMAGE_COUNTER");
        gamesWon = savedInstanceState.getInt("GAMES_WON");
        gamesTotal = savedInstanceState.getInt("GAMES_TOTAL");
        chosenCategoryIndex = savedInstanceState.getInt("CHOSEN_CATEGORY_INDEX");

        showLastImage();

        /* TODO: Hva man må vite/få info om:
            Hvilke bokstaver som er funnet i ordet man gjetter på
            Hvordan keyboardet ser ut (bokstaver røde el. grønne el. hvite)
        */
    }

    public void showLastImage() {
        if (res.getConfiguration().orientation == 1) // then it is ORIENTATION_PORTRAIT
        {
            // show next image in the ImageView main_image:
            ImageView imageView = (ImageView) findViewById(R.id.main_image);

            if(imageCounter == 0) { // the array IMAGE_IDS, that the imageCounter counts on, does not contain the first image
                imageView.setBackgroundResource(R.drawable.hangman_forste);
            } else {
                imageView.setBackgroundResource(IMAGE_IDS[imageCounter - 1]);
            }
        } else {    // then it is ORIENTATION_LANDSCAPE (2)
            // show next image in the LinearLayout image_layout_land:
            LinearLayout layout = (LinearLayout) findViewById(R.id.image_layout_land);

            if(imageCounter == 0) { // the array IMAGE_IDS, that the imageCounter counts on, does not contain the first image
                layout.setBackgroundResource(R.drawable.hangman_forste);
            } else {
                layout.setBackgroundResource(IMAGE_IDS[imageCounter - 1]);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        res = getResources();

        alphabetLetters = res.getStringArray(R.array.listAlphabet);

        if (savedInstanceState == null) {
            // Then: create pop-up to choose category:

            String[] categories = res.getStringArray(R.array.listCategories);

            // Pop-up-dialogs where the player chooses category:
            String catTitle = getString(R.string.choose_category);
            NewGameDialog catDialog = NewGameDialog.newInstance(catTitle, categories);
            catDialog.show(getFragmentManager(), "TAG");
            // Waiting for the player to make a choice
        }
    }

    // NewGameDialog-method:
    @Override
    public void onItemClick(int chosenItemIndex) {
        chosenCategoryIndex = chosenItemIndex;
        setWordsFromCategoryChoice();

        currentWord = getNextWord();

        if (currentWord != null) {
            // TODO: CREATEKEYBOARD FOR LANDSCAPE: if (res.getConfiguration().orientation == 1) // then it is ORIENTATION_PORTRAIT??
            createGuessWordArea(currentWord);
            createKeyboard();
        } else {
            endOfSessionDialog();
        }
    }

    // NewGameDialog-method:
    @Override
    public void onCancelClick() {
        finish();
    }

    // EndGameDialog-method:
    @Override
    public void onOkClick() {
        currentWord = getNextWord();

        if (currentWord != null) {
            // TODO: CREATEKEYBOARD FOR LANDSCAPE: if (res.getConfiguration().orientation == 1) // then it is ORIENTATION_PORTRAIT??
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

    // The user has chosen a category from the pop-up dialog NewGameDialog
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

    // TODO: CREATEKEYBOARD-METHOD FOR BOTH PORTRAIT AND LANDSCAPE? OR: JUST CHANGE THE TESTS
    public void createKeyboard() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_1);

        for (int i = 0; i < alphabetLetters.length; i++) {
            if (i >= NEW_LINE_KEYBOARD_FIRST && i < NEW_LINE_KEYBOARD_SECOND) {
                layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_2);
            } else if (i >= NEW_LINE_KEYBOARD_SECOND) {
                layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_3);
            }

            // Creating a button (one button for each letter) for the keyboard and adding it to
            // the specified layout:
            Button buttonLetter = new Button(this);
            buttonLetter.setId(i + 50);
            buttonLetter.setTextColor(Color.WHITE);
            buttonLetter.setTextSize(KEYBOARD_TEXT_SIZE);
            buttonLetter.setText(alphabetLetters[i]);
            buttonLetter.setBackgroundResource(R.drawable.custom_button);

            buttonLetter.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Button b = (Button) view;
                    char guessedLetter = b.getText().charAt(0);

                    // disable onclick on the button:
                    b.setEnabled(false);

                    if (correctGuessedLetter(guessedLetter)) {
                        // change color for the letter to green:
                        b.setTextColor(Color.GREEN);

                        if (numLettersGuessed >= currentWord.length())
                            // the word is guessed - the player won
                            endOfGameDialog(true);
                    } else {
                        // change color for the letter to red
                        b.setTextColor(Color.RED);

                        showNextImage();

                        // If this was the last image: weren't able to guess the word
                        // If now, after imageCounter++, imageCounter is the same as the length of
                        // the image-array, the last image has just been shown, and the player
                        // weren't able to guess the word
                        if (imageCounter >= IMAGE_IDS.length)
                            endOfGameDialog(false);
                    }
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(KEYBOARD_WIDTH, KEYBOARD_HEIGHT);
            layoutParams.setMargins(KEYBOARD_MARGIN, KEYBOARD_MARGIN, KEYBOARD_MARGIN, KEYBOARD_MARGIN);
            buttonLetter.setLayoutParams(layoutParams);

            layout.addView(buttonLetter);
        }
    }

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

    // TODO: clearKeyboard-method for both landscape and portrait - or if-test inside this
    public void clearKeyboard() {
        LinearLayout layoutRow1 = (LinearLayout) findViewById(R.id.keyboard_layout_row_1);
        LinearLayout layoutRow2 = (LinearLayout) findViewById(R.id.keyboard_layout_row_2);
        LinearLayout layoutRow3 = (LinearLayout) findViewById(R.id.keyboard_layout_row_3);

        layoutRow1.removeAllViews();
        layoutRow2.removeAllViews();
        layoutRow3.removeAllViews();
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
        if (res.getConfiguration().orientation == 1) // then it is ORIENTATION_PORTRAIT)
        {
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

        // Creating pop-up/dialog with title and message that has a NEW GAME-button and a
        // QUIT-button:
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
