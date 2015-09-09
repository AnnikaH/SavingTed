package com.example.s198541.s198611.savingted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
