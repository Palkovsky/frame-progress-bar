package pl.owsica.andrzej.rectangularprogressbar;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pl.owsica.andrzej.frameprogressbar.FrameProgressBar;
import pl.owsica.andrzej.frameprogressbar.utils.Direction;

public class MainActivity extends AppCompatActivity {

    FrameProgressBar rectangularProgressBar_1;
    FrameProgressBar rectangularProgressBar_2;

    FrameProgressBar rectangularProgressBar_4;
    TextView progressTv;
    Button clockwiseBtn;
    Button placeBtn;
    EditText editText;

    final int updateInterval = 2;

    float progress = 0;
    boolean clockwise = false;
    int place = Direction.TOP;
    int mIdetermineSpeed = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rectangularProgressBar_1 = (FrameProgressBar) findViewById(R.id.rectangularPb_1);
        rectangularProgressBar_2 = (FrameProgressBar) findViewById(R.id.rectangularPb_2);
        rectangularProgressBar_4 = (FrameProgressBar) findViewById(R.id.rectangularPb_4);

        progressTv = (TextView) findViewById(R.id.progressTv);

        editText = (EditText) findViewById(R.id.editText);

        editText.setMaxWidth(editText.getWidth());

        /*
        clockwiseBtn = (Button) findViewById(R.id.clockwiseBtn);
        clockwiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = 0;
                clockwise = !clockwise;
                update();
                rectangularProgressBar_2.setClockwise(clockwise);

                if (rectangularProgressBar_1.isVisible())
                    rectangularProgressBar_1.hide();
                else
                    rectangularProgressBar_1.show();

            }
        });

        placeBtn = (Button) findViewById(R.id.placeBtn);
        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = 0;
                place = Direction.nextDirection(place, true);
                rectangularProgressBar_2.setStartPlace(place);
                update();

                if(rectangularProgressBar_4.isVisible())
                    rectangularProgressBar_4.hide();
                else
                    rectangularProgressBar_4.show();
            }
        });
        */

        update();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    progress += 0.35f;
                    if (progress > 100)
                        progress = 0;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, updateInterval);
                }
            }
        };
        handler.postDelayed(runnable, updateInterval);
    }

    private void update() {
        rectangularProgressBar_1.setProgress(progress);
        rectangularProgressBar_1.setClockwise(clockwise);
        rectangularProgressBar_1.setStartPlace(place);

        /*
        progressTv.setText(String.format("%.0f", progress) + "%");

        if (clockwise)
            clockwiseBtn.setText("CLOCKWISE");
        else
            clockwiseBtn.setText("COUNTERCLOCKWISE");

        placeBtn.setText(Direction.toString(place));
        */

    }

}
