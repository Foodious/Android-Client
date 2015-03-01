package robert.com.foodious;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends Activity implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<FoodPlace>>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;
    private boolean loaded = false;
    FoodPlace foodPlace;
    private List<FoodPlace> mGlobalList;

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
        setTheme(R.style.AppTheme);
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

    public void randomizer(){
        TextView textView = (TextView) findViewById(R.id.textView);
        if (!mGlobalList.isEmpty())
        {
            int random = (int) ((Math.random() * 100) % mGlobalList.size());
            foodPlace = mGlobalList.get(random);
            mGlobalList.remove(random);
            String s = foodPlace.toString();
            textView.setText(s);
        }
        else
        {
            textView.setText("No more restaurants near you :'(");
        }
    }

    public View.OnClickListener randomButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            randomizer();

        }
    };

    @Override
    public Loader<List<FoodPlace>> onCreateLoader(int i, Bundle bundle) {
        return new FoodListDataLoader(this,null, mLongitude, mLatitude);
    }

    @Override
    public void onLoadFinished(Loader<List<FoodPlace>> listLoader, List<FoodPlace> foodPlaces) {
        if(foodPlaces!=null){
            loaded = true;
            mGlobalList = foodPlaces;
            randomizer();

            Button foodBtn = (Button) findViewById(R.id.FoodButton);
            foodBtn.setOnClickListener(randomButtonListener);
            foodBtn.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onLoaderReset(Loader<List<FoodPlace>> listLoader) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d("LocationShit", "mLastLocation is " + mLastLocation);

        if(mLastLocation != null){
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        }
        if(!loaded) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
            loaded = true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}


