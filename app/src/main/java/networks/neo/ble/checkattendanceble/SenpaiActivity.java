package networks.neo.ble.checkattendanceble;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.PupilModelAdapter;
import entity.Pupil;

public class SenpaiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TeacherFirebase";
    private DatabaseReference dbRef;
    private FirebaseUser user;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senpai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = dbRef.child("users");
        View headView = navigationView.getHeaderView(0);
        final TextView accountUsername = headView.findViewById(R.id.s_username);

        users.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.child("name").getValue();
                accountUsername.setText(username);
                Log.d(TAG, "Username is: " + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.");
            }
        });

        TextView accountEmail = headView.findViewById(R.id.s_email);
        accountEmail.setText(user.getEmail());

        listView = (ListView) findViewById(R.id.listView);

        ArrayList<Pupil> pupils = new ArrayList<>();
//        pupils.add(new Pupil("qwertyuiop", "someone", "123456"));
//        ArrayAdapter<Pupil> adapter = new ArrayAdapter<>(this, R.layout.pupils, pupils);
        PupilModelAdapter adapter = new PupilModelAdapter(this, pupils);
        listView.setAdapter(adapter);

        getPupils("TV-61", adapter, pupils);
//        ArrayAdapter<Pupil> adapter = new ArrayAdapter<>(this, R.layout.pupils, pupils);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_send) {
            FirebaseAuth.getInstance().signOut();   // End user session
            startActivity(new Intent(SenpaiActivity.this, AuthorizationActivity.class));  // Go back to start page
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listView);
    }

    private List<Pupil> getPupils(final String group, final PupilModelAdapter adapter, final List<Pupil> pupils) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.child("groups").child(group).getChildren();
                ArrayList<DataSnapshot> pupilsDS = Lists.newArrayList(dataSnapshotIterable);
                for (DataSnapshot pupil : pupilsDS) {
                    String pupilHash = (String) pupil.getValue();
                    Object users = dataSnapshot.child("users").getValue();
                    Object users1 = dataSnapshot.child("users").child(pupilHash).getValue();
                    Object users2 = dataSnapshot.child("users").child(pupilHash).child("name").getValue();
                    pupils.add(
                            new Pupil(pupilHash,
                                    dataSnapshot.child("users").
                                            child(pupilHash).
                                            child("name").
                                            getValue().
                                            toString(),
                                    dataSnapshot.child("users").child(pupilHash).child("SSID").getValue().toString())
                    );
                }

                adapter.notifyDataSetChanged();
                Log.d(TAG, "Pupils is: " + pupils);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.");
            }
        });

        return pupils;
    }
}
