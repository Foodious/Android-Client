package foodious.com.foodious;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Created by Robert on 12/23/2014.
 */
public class FoodListDataLoader extends AsyncTaskLoader<List<Restaurant>> {

    private final double mLatitude;
    private final double mLongitude;
    private StringBuilder apiCall;

    public FoodListDataLoader(Context context, double longitude, double latitude, StringBuilder apiCall) {
        super(context);
        mLatitude = latitude;
        mLongitude = longitude;
        this.apiCall = apiCall;
    }

    @Override
    public List<Restaurant> loadInBackground() {
        final List<Restaurant> restaurants = new ArrayList<Restaurant>();
        InputStream is = null;
        try {
            URL url = new URL(apiCall.toString());
            is = url.openConnection().getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (is != null)
        {
            JsonReader rdr = Json.createReader(is);
            JsonObject obj = rdr.readObject();
            JsonArray results  = obj.getJsonArray("results");
            try {
                for (int i = 0; i < results.size(); i++) {
                    Restaurant r = new Restaurant(results.getJsonObject(i));
                    if (!r.invalid) {
                        restaurants.add(r);
                    }
                }
            } catch (JsonException e) {
                e.printStackTrace();
            }
            return restaurants;
        }
        return null;
    }


}
