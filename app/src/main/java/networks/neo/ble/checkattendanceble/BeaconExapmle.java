package networks.neo.ble.checkattendanceble;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.messages.EddystoneUid;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import static com.google.android.gms.nearby.connection.Strategy.P2P_CLUSTER;

public class BeaconExapmle extends AppCompatActivity {
    protected static final String TAG = "BeaconsEverywhere";

    MessageListener mMessageListener;
    Message mMessage;

    Message mActiveMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_exapmle);
//    ...
        // Subscribe for all Eddystone UIDs whose first 10 bytes (the "namespace")
        // match MY_EDDYSTONE_UID_NAMESPACE.
        //
        // Note that the Eddystone UID namespace is separate from the namespace
        // field of a Nearby Message.

//        MessageFilter messageFilter = new MessageFilter.Builder()
//                .includeIBeaconIds()
//                .build();

        MessageFilter messageFilter = new MessageFilter.Builder()
                .includeEddystoneUids("e59305ebad7ed29fca69", null /* any instance */)
                .build();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .setFilter(messageFilter)
                .build();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                Log.i(TAG, "Found UID");
                // Note: Checking the type shown for completeness, but is unnecessary
                // if your message filter only includes a single type.
                if (Message.MESSAGE_NAMESPACE_RESERVED.equals(message.getNamespace())
                        && Message.MESSAGE_TYPE_EDDYSTONE_UID.equals(message.getType())) {
                    // Nearby provides the EddystoneUid class to parse Eddystone UIDs
                    // that have been found nearby.
                    EddystoneUid eddystoneUid = EddystoneUid.from(message);
                    Log.i(TAG, "Found Eddystone UID: " + eddystoneUid);
                }
            }
        };

        Nearby.getMessagesClient(this).subscribe(mMessageListener, options);
//    ...
//        mMessageListener = new MessageListener() {
//            @Override
//            public void onFound(Message message) {
//                // Do something with the message here.
//                Log.i(TAG, "Message found: " + message);
//                Log.i(TAG, "Message string: " + new String(message.getContent()));
//                Log.i(TAG, "Message namespaced type: " + message.getNamespace() +
//                        "/" + message.getType());
//            }
//
//            @Override
//            public void onLost(Message message) {
//                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
//            }
//        };

        mMessage = new Message("____ My beacon message ____".getBytes());
    }

    @Override
    public void onStart() {
        super.onStart();
//    ...
//        publish("____ My beacon message ____");
        subscribe();
//    ...
        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    public void onStop() {
        Nearby.getMessagesClient(this).unpublish(mMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
//    ...
//        unpublish();
        unsubscribe();
//    ...
        super.onStop();
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage);
    }

    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.getMessagesClient(this).unpublish(mActiveMessage);
            mActiveMessage = null;
        }
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();
//        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        Nearby.getMessagesClient(this).subscribe(mMessageListener, options);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
    }

    // Subscribe to messages in the background.
//    private void backgroundSubscribe() {
//        Log.i(TAG, "Subscribing for background updates.");
//        SubscribeOptions options = new SubscribeOptions.Builder()
//                .setStrategy(Strategy.BLE_ONLY)
//                .build();
//        Nearby.getMessagesClient(this).subscribe(getPendingIntent(), options);
//    }
//
//    private PendingIntent getPendingIntent() {
//        return PendingIntent.getBroadcast(this, 0, new Intent(this, BeaconMessageReceiver.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }

}
