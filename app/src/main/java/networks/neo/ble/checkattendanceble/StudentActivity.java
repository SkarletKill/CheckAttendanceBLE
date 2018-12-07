package networks.neo.ble.checkattendanceble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StudentActivity extends AppCompatActivity {
    ImageView imgButton;
    TextView textImgDescibe;
    boolean isSwitchOn;

    // - student id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        init();
        start();
    }

    private void init() {
        imgButton = findViewById(R.id.imgPower);
        textImgDescibe = findViewById(R.id.textOfButtonS);
        isSwitchOn = false;
    }

    public void start() {
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSwitchOn) {
                    // stop beacon
                    imgButton.setImageResource(R.drawable.power_button_red_white_inner);
                    textImgDescibe.setText(R.string.power_on);
                    isSwitchOn = false;
                } else {
                    // start beacon
                    imgButton.setImageResource(R.drawable.power_button_green_white_inner);
                    textImgDescibe.setText(R.string.power_off);
                    isSwitchOn = true;
                }
            }
        });
    }
}
