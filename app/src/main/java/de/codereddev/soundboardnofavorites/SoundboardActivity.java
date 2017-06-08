package de.codereddev.soundboardnofavorites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundboardActivity extends AppCompatActivity {

    // Define a tag that is used to log any kind of error or comment
    private static final String LOG_TAG = "SoundboardActivity";

    // Declare a toolbar to use instead of the system standard toolbar
    Toolbar toolbar;

    // Declare an ArrayList that you fill with SoundObjects that contain all information needed for a sound button
    ArrayList<SoundObject> soundList = new ArrayList<>();

    // Declare a RecyclerView and its components
    // You can assign the RecyclerView.Adapter right away
    RecyclerView SoundView;
    SoundboardRecyclerAdapter SoundAdapter = new SoundboardRecyclerAdapter(soundList);
    RecyclerView.LayoutManager SoundLayoutManager;

    // Declare a View that will contain the layout of the activity and serves as the parent of a Snackbar
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundboard);

        // Assign layout view
        // See: activity_soundboard.xml to change the id
        mLayout = findViewById(R.id.activity_soundboard);

        // Assign toolbar to the Toolbar item declared in activity_soundboard.xml
        toolbar = (Toolbar) findViewById(R.id.soundboard_toolbar);

        // Set toolbar as new action bar
        setSupportActionBar(toolbar);

        // Get all entries of the name StringArray(soundNames) declared in strings.xml
        List<String> nameList = Arrays.asList(getResources().getStringArray(R.array.soundNames));

        // Declare all sound buttons
        SoundObject[] soundItems = {new SoundObject(nameList.get(0), R.raw.audio01), new SoundObject(nameList.get(1), R.raw.audio02), new SoundObject(nameList.get(2), R.raw.audio03)};

        // Fill soundList with the information from soundItems
        soundList.addAll(Arrays.asList(soundItems));

        // Assign SoundView to the RecyclerView item declared in activity_soundboard.xml
        SoundView = (RecyclerView) findViewById(R.id.soundboardRecyclerView);

        // Define the RecyclerView.LayoutManager to have 3 columns
        SoundLayoutManager = new GridLayoutManager(this, 3);

        // Set the RecyclerView.LayoutManager
        SoundView.setLayoutManager(SoundLayoutManager);

        // Set the RecyclerView.Adapter
        SoundView.setAdapter(SoundAdapter);

        // Calls a method that handles all permission events
        requestPermissions();
    }

    // Takes care of some things when the user closes the activity
    @Override
    protected void onDestroy(){
        super.onDestroy();

        // Calls a method that releases all data from the used MediaPlayer instance
        EventHandlerClass.releaseMediaPlayer();
    }

    // Handles all permission events
    private void requestPermissions(){

        // Check if the users Android version is equal to or higher than Android 6 (Marshmallow)
        // Since Android 6 you have to request permissions at runtime to provide a better security
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            // Check if the permission to write and read the users external storage is not granted
            // You need this permission if you want to share sounds via WhatsApp or the like
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                // You can log this little text if you want to see if this method works in your Android Monitor
                //Log.i(LOG_TAG, "Permission not granted");

                // If the permission is not granted request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            // Check if the permission to write the users settings is not granted
            // You need this permission to set a sound as ringtone or the like
            if(!Settings.System.canWrite(this)){

                // Displays a little bar on the bottom of the activity with an OK button that will open a so called permission management screen
                Snackbar.make(mLayout, "The app needs access to your settings", Snackbar.LENGTH_INDEFINITE).setAction("OK",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Context context = v.getContext();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }

        }
    }
}
