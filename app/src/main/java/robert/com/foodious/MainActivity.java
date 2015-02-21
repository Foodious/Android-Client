package robert.com.foodious;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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
        /*try {
            final int titleId =
                    Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
            TextView title = (TextView) getWindow().findViewById(titleId);
            if (title != null)
            {
                String tempString="Foodious";
                SpannableString spanString = new SpannableString(tempString);
                spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
                title.setText(spanString);
            }
        } catch (Exception e) {
            Log.d("", "Failed to obtain action bar title reference");
        }*/
        setContentView(R.layout.activity_main);
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
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.reconnect();
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
        LinearLayout layout = (LinearLayout) findViewById(R.id.FoodList);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment placeList = new PlaceListFragment();
        Bundle args = new Bundle();

        placeList.setArguments(args);
        placeList.getArguments().putDouble("latitude", mLatitude);
        placeList.getArguments().putDouble("longitude", mLongitude);
        transaction.add(R.id.FoodList, placeList).commit();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}


