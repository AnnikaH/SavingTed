package com.example.s198541.s198611.savingted;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private static final int TEXT_SIZE_GUESS_WORD = 20;
    private static final int PADDING_GUESS_WORD = 15;
    private static final int NEW_LINE_KEYBOARD_FIRST = 9;
    private static final int NEW_LINE_KEYBOARD_SECOND = 19;
    private static final int TEXT_SIZE_KEYBOARD = 16;
    private static final int PADDING_KEYBOARD = 5;
    private static final int MARGIN_KEYBOARD = 2;
    private static final int WIDTH_KEYBOARD = 42;
    private static final int HEIGHT_KEYBOARD = 42;

    //private Resources res;
    private String[] words;
    private int index = 0;
    private String currentWord;
    private String[] alphabetLetters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Resources res = getResources();

        words = res.getStringArray(R.array.listWords);
        // shuffle words (random which word to guess first):
        List<String> shuffledList = Arrays.asList(words);
        Collections.shuffle(shuffledList);
        words = shuffledList.toArray(new String[shuffledList.size()]);

        alphabetLetters = res.getStringArray(R.array.listAlphabet);

        currentWord = getNextWord();

        if(currentWord != null) {
            createGuessWordArea(currentWord);
            createKeyboard();
        }
    }

    public String getNextWord() {
        if(index >= words.length) {
            endOfSession();
            return null;
        }

        return words[index++];
    }

    public void endOfSession() {
        // code for what's going to happen when all the words are guessed
    }

    public void createGuessWordArea(String chosenWord) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.guessing_word_layout);

        for(int i = 0; i < chosenWord.length(); i++) {
            TextView textViewLetter = new TextView(this);
            textViewLetter.setTextSize(TEXT_SIZE_GUESS_WORD);
            textViewLetter.setText("_");
            textViewLetter.setId(i);
            textViewLetter.setPadding(PADDING_GUESS_WORD, PADDING_GUESS_WORD, PADDING_GUESS_WORD, PADDING_GUESS_WORD);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewLetter.setLayoutParams(layoutParams);
            layout.addView(textViewLetter);
        }
    }

    public void createKeyboard() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_1);

        for(int i = 0; i < alphabetLetters.length; i++) {
            if(i >= NEW_LINE_KEYBOARD_FIRST && i < NEW_LINE_KEYBOARD_SECOND) {
                layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_2);
            }
            else if(i >= NEW_LINE_KEYBOARD_SECOND) {
                layout = (LinearLayout) findViewById(R.id.keyboard_layout_row_3);
            }

            Button buttonLetter = new Button(this);
            buttonLetter.setTextSize(TEXT_SIZE_KEYBOARD);
            buttonLetter.setText(alphabetLetters[i]);
            buttonLetter.setPadding(PADDING_KEYBOARD, PADDING_KEYBOARD, PADDING_KEYBOARD, PADDING_KEYBOARD);
            buttonLetter.setBackgroundResource(R.drawable.custom_button);
            buttonLetter.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Button b = (Button) view;
                    char guessedLetter = b.getText().charAt(0);
                    checkGuessedLetter(guessedLetter);
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WIDTH_KEYBOARD, HEIGHT_KEYBOARD);
            layoutParams.setMargins(MARGIN_KEYBOARD, MARGIN_KEYBOARD, MARGIN_KEYBOARD, MARGIN_KEYBOARD);
            buttonLetter.setLayoutParams(layoutParams);

            layout.addView(buttonLetter);
        }
    }

    public void checkGuessedLetter(char letter) {
        for(int i = 0; i < currentWord.length(); i++) {
            if(currentWord.charAt(i) == letter) {
                TextView textViewFoundLetter = (TextView) findViewById(i);
                textViewFoundLetter.setText(letter + "");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_front_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.new_game:
                Intent i = new Intent(this, GameActivity.class);
                startActivity(i);
                // ?:
                finish();
                return true;
            case R.id.rules:
                Intent i2 = new Intent(this, RulesActivity.class);
                startActivity(i2);
                // ?:
                finish();
                return true;
            case R.id.settings:
                Intent i3 = new Intent(this, SettingsActivity.class);
                startActivity(i3);
                // ?:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
