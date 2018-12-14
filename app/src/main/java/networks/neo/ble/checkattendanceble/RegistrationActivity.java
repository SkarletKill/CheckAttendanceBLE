package networks.neo.ble.checkattendanceble;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import constants.StringConstants;
import constants.UserType;

public class RegistrationActivity extends AppCompatActivity {
    private final String TAG = "RegistrationFirebase";

    private FirebaseAuth mAuth;

    private Button btn_reg;
    private EditText email, pass, username;
    //    RadioButton role;
    private RadioGroup roleGroup;
    private long userPosition;

    StringConstants SC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        init();
        register();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            updateUI(currentUser);
    }

    private void init() {
        email = findViewById(R.id.regLogin);
        pass = findViewById(R.id.password1);
        username = findViewById(R.id.username);
        roleGroup = findViewById(R.id.radioGroup);
        btn_reg = findViewById(R.id.btnRegister);
    }

    public void register() {
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roleGroup.getCheckedRadioButtonId() == R.id.rb_student) userPosition = 2;
                else if (roleGroup.getCheckedRadioButtonId() == R.id.rb_teacher) userPosition = 1;

                if (checkRegister()) {
                    mAuth.createUserWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success, update UI with the signed-up user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) writeToDB(user);
                                        updateUI(user);
                                    } else {
                                        // If sign up fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }

            }
        });
    }   // end register

    private void updateUI(Object currentUser) {
        final FirebaseUser user = (FirebaseUser) currentUser;
        if (user == null) {
            String message = "Registration failed!";
            Toast.makeText(RegistrationActivity.this, message,
                    Toast.LENGTH_LONG);
        } else {
            // goto user's page
            if (UserType.getType(userPosition) == null) {
                Log.d(TAG, "user pos == null");
            } else if (UserType.getType(userPosition).equals(UserType.TEACHER)) {
                Intent intent = new Intent(RegistrationActivity.this, SenpaiActivity.class);
                startActivity(intent);
            } else if (UserType.getType(userPosition).equals(UserType.STUDENT)) {
                Intent intent = new Intent(RegistrationActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        }
    }

    private void writeToDB(final FirebaseUser user) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//        dbRef.child("users").push().setValue(user.getUid());
        DatabaseReference childUser = dbRef.child("users").child(user.getUid());
        childUser.child("SSID").setValue(generateSSID());
        childUser.child("name").setValue(String.valueOf(username.getText()));
        childUser.child("position").setValue(userPosition);
    }

    private String generateSSID() {
//        String id_template = "00000000-0000-0000-0000-000000000000";
        String idNew = new StringBuilder()
                .append(create4Hex()).append(create4Hex())
                .append("-").append(create4Hex())
                .append("-").append(create4Hex())
                .append("-").append(create4Hex())
                .append("-").append(create4Hex()).append(create4Hex()).append(create4Hex()).toString();

        return idNew;
    }

    private String create4Hex() {
        String s4 = "";
        for (int i = 0; i < 4; i++) {
            int num = new Random().nextInt(15);
            String strNum;
            if (num < 10) strNum = String.valueOf(num);
            else {
                strNum = String.valueOf((char) ('a' + (num - 10)));
            }
            s4 += strNum;
        }
        return s4;
    }

    private boolean checkRegister() {
        String email = String.valueOf(this.email.getText());
        String pass = String.valueOf(this.pass.getText());
        String msg;
        if (email == null || email.isEmpty()) {
            msg = SC.LOGIN_IS_EMPTY;
        } else if (pass == null || pass.isEmpty()) {
            msg = SC.PASSWORD_IS_EMPTY;
        } else if (String.valueOf(username.getText()) == null || String.valueOf(username.getText()).isEmpty()) {
            msg = SC.NAME_IS_EMPTY;
        } else {
            return true;
        }
        Toast.makeText(
                RegistrationActivity.this, msg,
                Toast.LENGTH_LONG
        ).show();
        return false;
    }
}
