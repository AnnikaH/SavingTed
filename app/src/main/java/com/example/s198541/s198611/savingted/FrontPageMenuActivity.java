package com.example.s198541.s198611.savingted;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.InputStream;

public class FrontPageMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page_menu);
//      setContentView(new MyGIFView(getApplicationContext()));
//      getApplicationContext() possibly not working: MyApp.getContext();
//      MyApp skal tilsvare manifestfilen sin application: android:name="MyApp"
    }

//    private class MyGIFView extends View {
//        Movie movie;
//        InputStream is = null;
//        long moviestart;
//
//        public MyGIFView(Context context) {
//            super(context);
//            is = context.getResources().openRawResource(R.raw.hangman_background_animation);
//            movie = Movie.decodeStream(is);
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            canvas.drawColor(Color.WHITE);
//            super.onDraw(canvas);
//            long now = android.os.SystemClock.uptimeMillis();
//
//            if(moviestart == 0) {
//                moviestart = now;
//            }
//
//            int relTime = (int) ((now - moviestart) % movie.duration());
//            movie.setTime(2000);
//            movie.draw(canvas, this.getWidth() / 2 - 20, this.getHeight() / 2 - 40);
//            this.invalidate();
//        }
//    }

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
                return true;
            case R.id.rules:
                Intent i2 = new Intent(this, RulesActivity.class);
                startActivity(i2);
                return true;
            case R.id.settings:
                Intent i3 = new Intent(this, SettingsActivity.class);
                startActivity(i3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
