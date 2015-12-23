package moc5.amazingrace;

import android.app.Application;

/**
 * Created by Blade on 23.12.2015.
 */
public class AmazingRaceApp extends Application {

    Route selectedRoute;

    @Override
    public void onCreate() {
        super.onCreate();
        selectedRoute = null;
    }

    public void setSelectedRoute(Route route) {
        selectedRoute = route;
    }

    public Route getSelectedRoute() {
        return selectedRoute;
    }
}
