package foodious.com.foodious;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by danielmargosian on 3/15/15.
 */
public class AnalyticsHelper extends Application {
    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-60606989-1";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public AnalyticsHelper() {
        super();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
