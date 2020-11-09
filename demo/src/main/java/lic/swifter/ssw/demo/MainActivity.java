package lic.swifter.ssw.demo;

import android.os.Bundle;
import android.widget.Toast;

import lic.swifter.ssw.SlideSwitch;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SlideSwitch firstSwitch = findViewById(R.id.first_switch);
        final SlideSwitch secondSwitch = findViewById(R.id.second_switch);
        final SlideSwitch thirdSwitch = findViewById(R.id.third_switch);
        final SlideSwitch fourthSwitch = findViewById(R.id.fourth_switch);

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
                    if (thirdSwitch != null)
                        thirdSwitch.setState(true);
                }

                @Override
                public void close() {
                    Toast.makeText(MainActivity.this, "second switch close.", Toast.LENGTH_SHORT).show();
                    if (thirdSwitch != null)
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

        if (fourthSwitch != null)
            fourthSwitch.setSlideListener(new SlideSwitch.SlideListener() {
                @Override
                public void open() {
                    Toast.makeText(MainActivity.this, "fourth switch open.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void close() {
                    Toast.makeText(MainActivity.this, "fourth switch close.", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
