package robert.com.foodius;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.text.DateFormat;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends Activity implements PlaceListFragment.OnFragmentInteractionListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;
    private Button foodBtn;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foodBtn = (Button) findViewById(R.id.FoodButton);
        foodBtn.setOnClickListener(foodButtonListener);
        buildGoogleApiClient();

    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public View.OnClickListener foodButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            LinearLayout layout = (LinearLayout) findViewById(R.id.FoodList);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();


            Fragment placeList = new PlaceListFragment();


            Bundle args = new Bundle();

            placeList.setArguments(args);
            placeList.getArguments().putDouble("latitude", mLatitude);
            placeList.getArguments().putDouble("longitude", mLongitude);
            transaction.add(R.id.FoodList, placeList).commit();
        }
    };




    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d("LocationShit", "mLastLocation is " + mLastLocation);

        if(mLastLocation != null){
            findViewById(R.id.FoodButton).setVisibility(View.VISIBLE);
            Fragment foodList = getFragmentManager().findFragmentById(R.id.FoodList);
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}


