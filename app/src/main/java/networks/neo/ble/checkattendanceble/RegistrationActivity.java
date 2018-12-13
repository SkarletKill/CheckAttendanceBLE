package networks.neo.ble.checkattendanceble;

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

import constants.StringConstants;

public class RegistrationActivity extends AppCompatActivity {
    private final String TAG = "RegistrationFirebase";

    private FirebaseAuth mAuth;

    Button btn_reg;
    EditText email, pass, username;
    //    RadioButton role;
    RadioGroup roleGroup;

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
                mAuth.createUserWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, update UI with the signed-up user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign up fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegistrationActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });


//                String message = "Something wrong!";
//                if (String.valueOf(email.getText()).isEmpty()) {
//                    message = SC.LOGIN_IS_EMPTY;
//                } else if (String.valueOf(pass.getText()).isEmpty()) {
//                    message = SC.PASSWORD_IS_EMPTY;
//                } else if (!checkLogin()) {
//                    message = SC.LOGIN_IS_ALREADY_EXISTS;
//                } else if (!checkName()) {
//                    message = SC.SUCH_USER_NOT_FOUND;
//                } else {
//                    message = "";   // All right!
//                }
//
//                if (!message.isEmpty()) {
//                    Toast.makeText(
//                            RegistrationActivity.this, message,
//                            Toast.LENGTH_LONG
//                    ).show();
//                }
            }
        });
    }

    private void updateUI(Object currentUser) {
        FirebaseUser user = (FirebaseUser) currentUser;
        if (user == null) {
            String message = "Registration failed!";
            Toast.makeText(RegistrationActivity.this, message,
                    Toast.LENGTH_LONG);
        } else {
            // goto user's page
        }
    }

    private boolean checkLogin() {

        return false;
    }

    private boolean checkName() {
        RadioButton role = findViewById(roleGroup.getCheckedRadioButtonId());
        if (String.valueOf(role.getText()).equals(SC.TEACHER)) {
            // find teacher name (username)
        } else if (String.valueOf(role.getText()).equals(SC.STUDENT)) {
            // find student name (username)
        }
        return false;
    }
}
