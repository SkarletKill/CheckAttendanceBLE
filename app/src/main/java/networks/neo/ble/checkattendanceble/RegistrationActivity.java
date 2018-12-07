package networks.neo.ble.checkattendanceble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import constants.StringConstants;

public class RegistrationActivity extends AppCompatActivity {
    Button btn_reg;
    EditText login, pass, username;
    //    RadioButton role;
    RadioGroup roleGroup;

    StringConstants SC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        auth();
    }

    private void init() {
        login = findViewById(R.id.regLogin);
        pass = findViewById(R.id.password1);
        username = findViewById(R.id.username);
        roleGroup = findViewById(R.id.radioGroup);
        btn_reg = findViewById(R.id.btnRegister);
    }

    public void auth() {
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Something wrong!";
                if (String.valueOf(login.getText()).isEmpty()) {
                    message = SC.LOGIN_IS_EMPTY;
                } else if (String.valueOf(pass.getText()).isEmpty()) {
                    message = SC.PASSWORD_IS_EMPTY;
                } else if (!checkLogin()) {
                    message = SC.LOGIN_IS_ALREADY_EXISTS;
                } else if (!checkName()) {
                    message = SC.SUCH_USER_NOT_FOUND;
                } else {
                    message = "";   // All right!
                }

                if (!message.isEmpty()) {
                    Toast.makeText(
                            RegistrationActivity.this, message,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
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
