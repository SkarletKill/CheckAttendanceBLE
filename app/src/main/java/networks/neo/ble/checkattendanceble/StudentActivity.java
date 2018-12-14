package networks.neo.ble.checkattendanceble;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import constants.UserType;
import entity.User;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "StudentFirebase";
    private static final String BEACON_TEMPLATE = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private DatabaseReference dbRef;
    private FirebaseUser user;

    private ImageView imgButton;
    private TextView textImgDescibe;
    private boolean isSwitchOn;

    private Beacon beacon;
    private BeaconTransmitter beaconTransmitter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        View headView = navigationView.getHeaderView(0);
        final TextView accountUsername = headView.findViewById(R.id.account_username);

        dbRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
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

        TextView accountEmail = headView.findViewById(R.id.account_email);
        accountEmail.setText(user.getEmail());

        init();
        start();
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

        if (id == R.id.nav_gallery) {
            FirebaseAuth.getInstance().signOut();   // End user session
            startActivity(new Intent(StudentActivity.this, AuthorizationActivity.class));  // Go back to start page
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        imgButton = findViewById(R.id.imgPower);
        textImgDescibe = findViewById(R.id.textOfButtonS);
        isSwitchOn = false;

        dbRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ssid = (String) dataSnapshot.child("SSID").getValue();
                Log.d(TAG, "SSID is: " + ssid);

                beacon = new Beacon.Builder()
                        .setId1(ssid)
                        .setId2("5")
                        .setId3("9")
                        .setManufacturer(0x0118)
                        .setTxPower(-59)
                        .setDataFields(Arrays.asList(new Long[]{0l}))
                        .build();
                BeaconParser beaconParser = new BeaconParser()
                        .setBeaconLayout(BEACON_TEMPLATE);
                beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.");
            }
        });
    }

    public void start() {
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSwitchOn) {
                    // stop beacon
                    imgButton.setImageResource(R.drawable.power_button_red_white_inner);
                    textImgDescibe.setText(R.string.power_off);
                    isSwitchOn = false;

                    // beacon transmitting
                    beaconTransmitter.stopAdvertising();
                    Log.d(TAG, "stop beacon!");
                } else {
                    // start beacon
                    imgButton.setImageResource(R.drawable.power_button_green_white_inner);
                    textImgDescibe.setText(R.string.power_on);
                    isSwitchOn = true;

                    beaconTransmitter.startAdvertising(beacon);
                    Log.d(TAG, "start beacon!");
                }
            }
        });
    }

//    private String getParsedId() {
////        String id = "00000000-0000-0000-0000-000000000000";
//        final String id[] = new String[1];
//        dbRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String ssid = (String) dataSnapshot.child("SSID").getValue();
//                id[0] = ssid;
//                Log.d(TAG, "SSID is: " + ssid);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.");
//            }
//        });
//
//        return id[0];
//    }
//
//    private void writeValue(String[] to, String from) {
//        to[0] = from;
//    }

}
