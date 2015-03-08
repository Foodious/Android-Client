package robert.com.foodious;

        import android.content.AsyncTaskLoader;
        import android.content.Context;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.net.MalformedURLException;
        import java.net.URISyntaxException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by Robert on 12/23/2014.
 */
public class FoodListDataLoader extends AsyncTaskLoader<List<FoodPlace>> {

    private final Context mContext;
    private final URL mUrl;
    private final double mLatitude;
    private final double mLongitude;

    public FoodListDataLoader(Context context, URL url, double longitude, double latitude) {
        super(context);
        mContext = context;
        mUrl = url;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public List<FoodPlace> loadInBackground() {
        String data = null;
        final List<FoodPlace> foodPlaces = new ArrayList<FoodPlace>();
        final YelpAPI yelp = new YelpAPI("YMl-kgcfLZx4gZD5u8_F-A","zUOwQydRroPzJbOwviwqY7R5SUg","4QOuLvpnFenNxzReWcSaOiFKy699YuM3","h8NLol4R7QNKEqBbgJcsoaLy5Rc");
        final String string = yelp.searchForBusinessesByLocation("food", Double.toString(mLatitude), Double.toString(mLongitude));
        if(!string.isEmpty() || string != null) {
            try {
                final JSONObject response = new JSONObject(string);
                final JSONArray responseArray = response.getJSONArray("businesses");
                for (int i = 0; i < responseArray.length(); i++) {
                    final JSONObject currentObject = responseArray.getJSONObject(i);
                    final FoodPlace current = new FoodPlace(currentObject);
                    foodPlaces.add(current);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


            Log.d("APIResponse", string);
            return foodPlaces;
        }
        return null;
    }


}
