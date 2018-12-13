package networks.neo.ble.checkattendanceble;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import constants.StringConstants;

public class AuthorizationActivity extends AppCompatActivity {
    private final String TAG = "AuthorisationFirebase";
    private FirebaseAuth mAuth;

    Button btn_log, btn_reg;
    EditText email, pass;

    StringConstants SC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        mAuth = FirebaseAuth.getInstance();
        init();
        login();
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
        email = findViewById(R.id.authlogin);
        pass = findViewById(R.id.password);
        btn_log = findViewById(R.id.btnLogin);
        btn_reg = findViewById(R.id.btnGotoRegister);
    }

    private void updateUI(Object currentUser) {
        FirebaseUser user = (FirebaseUser) currentUser;
        if (user == null) {
            String message = "Authorization failed!";
            Toast.makeText(AuthorizationActivity.this, message,
                    Toast.LENGTH_LONG);
        } else {
            // goto user's page
        }
    }

    public void login() {
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                        .addOnCompleteListener(AuthorizationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(AuthorizationActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });


//                if (String.valueOf(email.getText()).equals(SC.LOGIN_TEACHER) && String.valueOf(pass.getText()).equals(SC.EMPTY)) {
//                    // goto teacher page
//                    Intent intent = new Intent(".TeacherActivity");
//                    startActivity(intent);
//                } else if (String.valueOf(email.getText()).equals(SC.LOGIN_STUDENT) && String.valueOf(pass.getText()).equals(SC.EMPTY)) {
//                    // goto student page
//                    Intent intent = new Intent(".StudentActivity");
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(
//                            AuthorizationActivity.this, SC.WRONG_LOGIN_OR_PASS,
//                            Toast.LENGTH_LONG
//                    ).show();
//                }
            }
        });
    }

    public void register() {
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(".RegistrationActivity");
                startActivity(intent);

//                Intent intent = new Intent(".BeaconActivity");
//                Intent intent = new Intent(".BeaconExapmle");
//                startActivity(intent);
            }
        });
    }
}
