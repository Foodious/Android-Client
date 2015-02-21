package robert.com.foodious;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Robert on 12/23/2014.
 */
public class FoodPlace implements Serializable{
    String placeName;

    public FoodPlace(JSONObject json) throws JSONException {
        placeName = json.getString("name");
    }

    public FoodPlace(FoodPlace fP) { placeName = fP.placeName; }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof FoodPlace)
        {
            return placeName.equals(((FoodPlace) o).placeName);
        }
        return false;
    }

    @Override
    public String toString(){
        return placeName;
    }


}
