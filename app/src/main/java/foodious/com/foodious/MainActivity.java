package foodious.com.foodious;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements PreferencesDialogFragment.PreferencesDialogListener, LoaderManager.LoaderCallbacks<List<Restaurant>>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    protected Dialog mSplashDialog;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;
    private boolean loaded = false;
    private Restaurant restaurant;
    private List<Restaurant> mGlobalList;
    private List<Restaurant> removed;
    private boolean picky;
    private StringBuilder apiCall;

    private static int radius = 5;

    private static final String LOADED = "loaded";

    private static final String API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String TYPE_FILTERS = "&types=meal_takeaway|restaurant";
    //include delivery (meal_delivery) maybe? remove bar or takeaway maybe? food too general?
    //bar is removed for now

    private static final String PUBLIC_API_KEY = "&key=AIzaSyDm8z7bJdV5-KWanFjPT56l7KMjdgkftbg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSplashScreen();
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        //checkForLocationServices();
        buildGoogleApiClient();
        removed = new ArrayList<Restaurant>();
    }

    protected void showSplashScreen() {
        mSplashDialog = new Dialog(this, R.style.splashScreenTheme);
        mSplashDialog.setContentView(R.layout.splash_screen);
        mSplashDialog.setCancelable(false);
        mSplashDialog.show();
    }

    protected void removeSplashScreen() {
        if (mSplashDialog != null) {
            mSplashDialog.dismiss();
            mSplashDialog = null;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //checkForLocationServices();
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
        checkForLocationServices();
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
        if (id == R.id.action_preferences) {
            DialogFragment dialog = new PreferencesDialogFragment();
            dialog.show(getFragmentManager(), "PreferencesDialogFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(PreferencesDialogFragment preferencesDialogFragment){
        if (preferencesDialogFragment.isChanged())
        {
            radius = preferencesDialogFragment.getRadius();
            mGlobalList.clear();
            buildAPIRequest();
            getLoaderManager().restartLoader(0, null, this).forceLoad();
            loaded = true;
            preferencesDialogFragment.setChanged(false);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void randomizer(){
        TextView restaurantName = (TextView) findViewById(R.id.restaurantName);
        ImageView restaurantImage = (ImageView) findViewById(R.id.restaurantImage);
        TextView price = (TextView) findViewById(R.id.price);
        ImageView restaurantRating = (ImageView) findViewById(R.id.rating);


        if (!mGlobalList.isEmpty()) {
            int random = (int) ((Math.random() * 100) % mGlobalList.size());
            restaurant = mGlobalList.get(random);
            removed.add(restaurant);
            mGlobalList.remove(random);
            Drawable d = restaurantImage.getDrawable();
            Picasso.with(this).load("https://maps.googleapis.com/maps/api/place/photo?" + "maxwidth=400" + "&maxheight=400" + "&photoreference=" + restaurant.photo + PUBLIC_API_KEY).into(restaurantImage);
            restaurantName.setText(restaurant.name);
            String money = "";
            for (int i = 0; i < restaurant.priceLevel; i++) {
                money += "$";
            }
            price.setText(money);

            double rating = Math.round(restaurant.rating * 2.0) / 2.0;

            if (rating != -1)
            {
                if (rating == 1.0)
                {
                    Picasso.with(this).load(R.drawable.star_1_rating).into(restaurantRating);
                }
                if (rating == 1.5)
                {
                    Picasso.with(this).load(R.drawable.star_1half_rating).into(restaurantRating);
                }
                if (rating == 2.0)
                {
                    Picasso.with(this).load(R.drawable.star_2_rating).into(restaurantRating);
                }
                if (rating == 2.5)
                {
                    Picasso.with(this).load(R.drawable.star_2half_rating).into(restaurantRating);
                }
                if (rating == 3.0)
                {
                    Picasso.with(this).load(R.drawable.star_3_rating).into(restaurantRating);
                }
                if (rating == 3.5)
                {
                    Picasso.with(this).load(R.drawable.star_3half_rating).into(restaurantRating);
                }
                if (rating == 4.0)
                {
                    Picasso.with(this).load(R.drawable.star_4_rating).into(restaurantRating);
                }
                if (rating == 4.5)
                {
                    Picasso.with(this).load(R.drawable.star_4half_rating).into(restaurantRating);
                }
                if (rating == 5.0)
                {
                    Picasso.with(this).load(R.drawable.star_5_rating).into(restaurantRating);
                }
            }

            updateMap();
        }
        else
        {
            picky = true;
            restaurant = null;
            restaurantName.setText("No more restaurants near you :'(\nHit 'Explore!' to start over!\n" +
                    "Alternatively, try widening your search radius!");
            restaurantImage.setVisibility(View.INVISIBLE);
            findViewById(R.id.map).setVisibility(View.INVISIBLE);
            findViewById(R.id.rating).setVisibility(View.INVISIBLE);
            price.setVisibility(View.INVISIBLE);
        }
    }

    public View.OnClickListener randomButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (picky)
            {
                while (!removed.isEmpty())
                {
                    mGlobalList.add(removed.get(0));
                    removed.remove(0);
                }
                findViewById(R.id.restaurantImage).setVisibility(View.VISIBLE);
                findViewById(R.id.map).setVisibility(View.VISIBLE);
                findViewById(R.id.rating).setVisibility(View.VISIBLE);
                findViewById(R.id.price).setVisibility(View.VISIBLE);
                picky = false;
            }
            randomizer();

        }
    };

    @Override
    public Loader<List<Restaurant>> onCreateLoader(int i, Bundle bundle) {
        return new FoodListDataLoader(this, mLongitude, mLatitude, apiCall);
    }

    @Override
    public void onLoadFinished(Loader<List<Restaurant>> listLoader, List<Restaurant> restaurants) {
        if(restaurants !=null){
            loaded = true;
            mGlobalList = restaurants;
            randomizer();

            Button foodBtn = (Button) findViewById(R.id.FoodButton);
            foodBtn.setOnClickListener(randomButtonListener);
            LinearLayout topLayout = (LinearLayout) findViewById(R.id.topLayout);
            topLayout.setVisibility(View.VISIBLE);
            removeSplashScreen();
        }


    }

    @Override
    public void onLoaderReset(Loader<List<Restaurant>> listLoader) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        updateLocation();
        if(!loaded) {

            buildAPIRequest();
            getLoaderManager().initLoader(0, null, this).forceLoad();
            loaded = true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        checkForLocationServices();

    }

    public void buildAPIRequest()
    {
        apiCall = new StringBuilder(API_BASE);
        apiCall.append("location=" + mLatitude + "," + mLongitude);
        apiCall.append("&radius=" + radius/10.0 * 1609.34);
        apiCall.append(TYPE_FILTERS);
        apiCall.append("&opennow");
        apiCall.append(PUBLIC_API_KEY);
    }

    public static double getRadius() {return radius;}

    public void updateMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        //Center the map on the location
        map.clear();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        updateLocation();

        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MarkerOptions you = new MarkerOptions();
        you.position(latLng);
        you.title("you");
        you.draggable(false);

        MarkerOptions restaurantMarker = new MarkerOptions();
        restaurantMarker.position(restaurant.latLng);
        restaurantMarker.title(restaurant.name.toString());
        restaurantMarker.draggable(false);
        restaurantMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.star_yellow));
        //restaurantMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        map.addMarker(you);
        map.addMarker(restaurantMarker);
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        b.include(latLng);
        b.include(restaurant.latLng);
        LatLngBounds bounds = b.build();
        //Change the padding as per needed
        View v = findViewById(R.id.map);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, v.getWidth()-100, v.getHeight()-300, 0);
        map.moveCamera(cu);
    }

    public boolean checkForLocationServices()
    {
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
            return false;
        }
        return true;
    }

    public void updateLocation()
    {
        if(mLastLocation != null){
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        }
        else
        {
            checkForLocationServices();
            updateLocation();
            mGoogleApiClient.reconnect();
        }

    }
}


