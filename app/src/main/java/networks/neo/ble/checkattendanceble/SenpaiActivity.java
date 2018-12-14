package networks.neo.ble.checkattendanceble;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import adapter.PupilModelAdapter;
import entity.Pupil;
import entity.User;

import static constants.BeaconTemplates.IBEACON;

public class SenpaiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeaconConsumer {
    private static final String TAG = "TeacherFirebase";
    private static final String TITLE = "Teacher's room";
    private DatabaseReference dbRef;
    private FirebaseUser user;

    private BeaconManager beaconManager;
    private List<String> groups;
    private String currentGroup;
    private List<Pupil> pupils;
    private List<Pupil> present;
    private List<Pupil> absent;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TITLE + " : All");

        setContentView(R.layout.activity_senpai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scanning...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                beaconManager.bind(SenpaiActivity.this);

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

        final ArrayList<Pupil> pupils = new ArrayList<>();
        this.pupils = pupils;
//        pupils.add(new Pupil("qwertyuiop", "someone", "123456"));
//        ArrayAdapter<Pupil> adapter = new ArrayAdapter<>(this, R.layout.pupils, pupils);
        final PupilModelAdapter adapter = new PupilModelAdapter(this, pupils);
        listView.setAdapter(adapter);

        groups = new ArrayList<>();
        DatabaseReference teachers = dbRef.child("teachers");
        teachers.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> groupIDS = dataSnapshot.getChildren();
                ArrayList<DataSnapshot> groupsDS = Lists.newArrayList(groupIDS);
                for (DataSnapshot group : groupsDS) {
                    String oneGroup = (String) group.getValue();
                    groups.add(oneGroup);
                }

                if (!groups.isEmpty()) {
                    currentGroup = groups.get(0);
                    getPupils(currentGroup, adapter, pupils);
                }

                Log.d(TAG, "Groups is: " + groups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.");
            }
        });

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON));
        beaconManager.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            setTitle(TITLE + " : Present");
//            if (present == null)
                present = pupils.stream().filter(Pupil::isPresent).collect(Collectors.toCollection(ArrayList::new));
            PupilModelAdapter adapter = new PupilModelAdapter(SenpaiActivity.this, present);
            listView.setAdapter(adapter);
        } else if (id == R.id.nav_gallery) {
            setTitle(TITLE + " : Absent");
//            if (absent == null)
                absent = pupils.stream().filter(p -> !p.isPresent()).collect(Collectors.toCollection(ArrayList::new));
            PupilModelAdapter adapter = new PupilModelAdapter(SenpaiActivity.this, absent);
            listView.setAdapter(adapter);
        } else if (id == R.id.nav_slideshow) {
            setTitle(TITLE + " : All");
            PupilModelAdapter adapter = new PupilModelAdapter(SenpaiActivity.this, pupils);
            listView.setAdapter(adapter);
        } else if (id == R.id.nav_manage) {
            // stop scan
            beaconManager.unbind(this);
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
//        listView = (ListView) findViewById(R.id.listView);
    }

    private List<Pupil> getPupils(final String group, final PupilModelAdapter adapter, final List<Pupil> pupils) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.child("groups").child(group).getChildren();
                ArrayList<DataSnapshot> pupilsDS = Lists.newArrayList(dataSnapshotIterable);
                for (DataSnapshot pupil : pupilsDS) {
                    String pupilHash = (String) pupil.getValue();
//                    Object users = dataSnapshot.child("users").getValue();
//                    Object users1 = dataSnapshot.child("users").child(pupilHash).getValue();
//                    Object users2 = dataSnapshot.child("users").child(pupilHash).child("name").getValue();
                    pupils.add(
                            new Pupil(pupilHash,
                                    (String) dataSnapshot.child("users").
                                            child(pupilHash).
                                            child("name").
                                            getValue(),
                                    (String) dataSnapshot.child("users").child(pupilHash).child("SSID").getValue())
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

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeacons", null, null, null);

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                if (state == 0) Log.i(TAG, "I have just switched to seeing/not seeing beacons");
                else if (state == 1)
                    Log.i(TAG, "I have just switched to NOT seeing beacons");
//                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (SenpaiActivity.this.currentGroup != null) {
                    // take students IDs in one group
                    final List<String> members = new ArrayList<>();
                    DatabaseReference currentGroup = dbRef.child("groups").child(SenpaiActivity.this.currentGroup);
                    currentGroup.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> memberIDS = dataSnapshot.getChildren();
                            ArrayList<DataSnapshot> membersDS = Lists.newArrayList(memberIDS);
                            for (DataSnapshot member : membersDS) {
                                String memberID = (String) member.getValue();
                                members.add(memberID);
                            }

                            Log.d(TAG, "Members is: " + members);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.");
                        }
                    });

                    // take student's SSIDs in group
                    final List<Pupil> pupils = new ArrayList<>();
                    DatabaseReference users = dbRef.child("users");
                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pupils.clear();
                            for (String member : members) {
                                String ssid = (String) dataSnapshot.child(member).child("SSID").getValue();
                                String name = (String) dataSnapshot.child(member).child("name").getValue();
//                                Log.d(TAG, "SSID is: " + ssid);
//                                Log.d(TAG, "Name is: " + name);
                                pupils.add(new Pupil(member, name, ssid));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.");
                        }
                    });

                    // search beacons
                    for (Beacon oneBeacon : beacons) {
                        Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());

                        // find them beacons
                        Pupil found = getPupil(SenpaiActivity.this.pupils, oneBeacon.getId1().toString());
                        if (found != null) {
                            Log.d(TAG, "FOUND!!!!!!!!!");
                            found.setPresent(true);
                        }

                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
//            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Pupil getPupil(List<Pupil> pupils, String beaconID) {
        for (Pupil pupil : pupils) {
            if (pupil.getSSID().equals(beaconID)) return pupil;
        }
        return null;
    }
}
