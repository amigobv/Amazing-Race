package moc5.amazingrace;

import android.app.Application;

/**
 * Created by Blade on 23.12.2015.
 */
public class AmazingRace extends Application {

    Route selectedRoute;
    Request authentification;

    @Override
    public void onCreate() {
        super.onCreate();
        selectedRoute = null;
        authentification = new Request();
        authentification.setUserName("s1310307036");
        authentification.setPassword("s1310307036");
    }

    public void setSelectedRoute(Route route) {
        selectedRoute = route;
    }

    public Route getSelectedRoute() {
        return selectedRoute;
    }

    public void setAuthentification(String username, String password)
    {
        authentification.setUserName(username);
        authentification.setPassword(password);
    }

    public Request getAuthentification() {
        return authentification;
    }
}
