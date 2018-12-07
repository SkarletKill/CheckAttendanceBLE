package networks.neo.ble.checkattendanceble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import constants.StringConstants;

public class AuthorizationActivity extends AppCompatActivity {
    Button btn_log, btn_reg;
    EditText login, pass;

    StringConstants SC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        init();
        login();
        register();
    }

    private void init() {
        login = findViewById(R.id.authlogin);
        pass = findViewById(R.id.password);
        btn_log = findViewById(R.id.btnLogin);
        btn_reg = findViewById(R.id.btnGotoRegister);
    }

    public void login() {
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(login.getText()).equals(SC.LOGIN_TEACHER) && String.valueOf(pass.getText()).equals(SC.EMPTY)) {
                    // goto teacher page
                    Intent intent = new Intent(".TeacherActivity");
                    startActivity(intent);
                } else if (String.valueOf(login.getText()).equals(SC.LOGIN_STUDENT) && String.valueOf(pass.getText()).equals(SC.EMPTY)) {
                    // goto student page
                    Intent intent = new Intent(".StudentActivity");
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            AuthorizationActivity.this, SC.WRONG_LOGIN_OR_PASS,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    public void register() {
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(".RegistrationActivity");
                startActivity(intent);
            }
        });
    }
}
