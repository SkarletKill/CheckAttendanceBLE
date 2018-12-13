package networks.neo.ble.checkattendanceble;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class StudentActivity extends AppCompatActivity {
    private static final String TAG = "StudentDB";
    private DatabaseReference dbRef;

    private ImageView imgButton;
    private TextView textImgDescibe;
    private boolean isSwitchOn;

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
        dbRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        dbRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long position = (Long) dataSnapshot.child("position").getValue();
                String name = (String) dataSnapshot.child("name").getValue();
//                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Student position is: " + position);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.");
            }
        });
    }

    private void testFDB() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
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
