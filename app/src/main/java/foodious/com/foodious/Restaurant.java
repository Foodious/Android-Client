package foodious.com.foodious;

import android.net.Uri;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Created by danielmargosian on 3/27/15.
 */
public class Restaurant implements Place, Serializable {

    String photo;

    CharSequence address;
    String id;
    LatLng latLng;
    Locale locale;
    CharSequence name;
    CharSequence phoneNumber;
    List<Integer> placeTypes;
    int priceLevel;
    float rating;
    LatLngBounds viewport;
    Uri websiteUri;

    boolean invalid;

    public Restaurant(JsonObject json) {
        id = json.getString("place_id");
        JsonObject location = json.getJsonObject("geometry").getJsonObject("location");
        latLng =  new LatLng(location.getJsonNumber("lat").doubleValue(), location.getJsonNumber("lng").doubleValue());
        name = json.getString("name");
        try {
            priceLevel = json.getInt("price_level");

        } catch (NullPointerException e) {
            priceLevel = 0;
        }
        try {
            rating = (float) json.getJsonNumber("rating").doubleValue();
        } catch (NullPointerException e) {
            rating = -1;
        }
        JsonArray photos = json.getJsonArray("photos");
        if (photos != null) {
            JsonObject photosJsonObject = photos.getJsonObject(0);
            photo = photosJsonObject.getString("photo_reference");
        }
        else
            invalid = true;


    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Restaurant) {
            Restaurant that = (Restaurant) o;
            return this.id.equals(that.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return name.toString();
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    @Override
    public CharSequence getAddress() {
        return address;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public CharSequence getName() {
        return name;
    }

    @Override
    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public LatLngBounds getViewport() {
        return viewport;
    }

    @Override
    public Uri getWebsiteUri() {
        return websiteUri;
    }

    @Override
    public CharSequence getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean zzpI() {
        return false;
    }

    @Override
    public float getRating() {
        return rating;
    }

    @Override
    public int getPriceLevel() {
        return priceLevel;
    }

    @Override
    public Place freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
