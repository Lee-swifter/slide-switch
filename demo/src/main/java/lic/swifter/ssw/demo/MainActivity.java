package lic.swifter.ssw.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import lic.swifter.demo.SlideSwitch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlideSwitch firstSwitch = (SlideSwitch) findViewById(R.id.first_switch);
        SlideSwitch secondSwitch = (SlideSwitch) findViewById(R.id.second_switch);
        final SlideSwitch thirdSwitch = (SlideSwitch) findViewById(R.id.third_switch);

        if (firstSwitch != null) {
            firstSwitch.setSlideListener(new SlideSwitch.SlideListener() {
                @Override
                public void open() {
                    Toast.makeText(MainActivity.this, "first switch open.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void close() {
                    Toast.makeText(MainActivity.this, "first switch close.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (secondSwitch != null) {
            secondSwitch.setSlideListener(new SlideSwitch.SlideListener() {
                @Override
                public void open() {
                    Toast.makeText(MainActivity.this, "second switch open.", Toast.LENGTH_SHORT).show();
                    if(thirdSwitch != null)
                        thirdSwitch.setState(true);
                }

                @Override
                public void close() {
                    Toast.makeText(MainActivity.this, "second switch close.", Toast.LENGTH_SHORT).show();
                    if(thirdSwitch != null)
                        thirdSwitch.setState(false);
                }
            });
        }
        if (thirdSwitch != null) {
            thirdSwitch.setSlideListener(new SlideSwitch.SlideListener() {
                @Override
                public void open() {
                    Toast.makeText(MainActivity.this, "third switch open.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void close() {
                    Toast.makeText(MainActivity.this, "third switch close.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
