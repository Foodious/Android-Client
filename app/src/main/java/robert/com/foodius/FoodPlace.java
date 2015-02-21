package robert.com.foodius;

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

    @Override
    public String toString(){
        return placeName;
    }


}
