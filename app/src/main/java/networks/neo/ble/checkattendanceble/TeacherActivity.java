package networks.neo.ble.checkattendanceble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TeacherActivity extends AppCompatActivity {
    private ListView studentList;
    private String[] students = new String[]{"Yuriy", "Vlad", "Guseva"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        init();
        list();
    }

    private void init() {

    }

    public void list() {
        studentList = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.students, students);
        studentList.setAdapter(adapter);

        studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = (String) studentList.getItemAtPosition(i);
                Toast.makeText(
                        TeacherActivity.this,
                        "Position: " + i + ", name: " + name,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

    }

    private void getGroups() {

    }
}
