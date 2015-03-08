package robert.com.foodious;

import android.media.Image;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Robert on 12/23/2014.
 */
public class FoodPlace implements Serializable{
    public String placeName;
    public Uri imageUri;
    public double rating;
    public double distance;

    public FoodPlace(JSONObject json) throws JSONException, URISyntaxException {

        placeName = json.getString("name");
        String uri = json.getString("image_url");
        distance = json.getDouble("distance");
        uri = uri.substring(0,uri.length()-6);
        uri += "o.jpg";
        imageUri = Uri.parse(uri);
        rating = json.getDouble("rating");
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof FoodPlace)
        {
            FoodPlace that = (FoodPlace) o;
            return this.placeName.equals(that.placeName) && this.imageUri.equals(that.imageUri) &&
                    this.rating == that.rating && this.distance == that.distance;
        }
        return false;
    }

    @Override
    public String toString(){
        return placeName;
    }


}
