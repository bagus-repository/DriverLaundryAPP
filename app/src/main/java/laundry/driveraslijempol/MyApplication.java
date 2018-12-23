package laundry.driveraslijempol;

import android.app.Application;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import laundry.driveraslijempol.utils.SessionManager;

/**
 * Created by Bagus on 02/08/2018.
 */
public class MyApplication extends Application {

    SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
                    @Override
                    public void notificationOpened(OSNotificationOpenResult result) {

                    }
                })
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification notification) {

                    }
                })
                .init();
        sessionManager = new SessionManager(this);
        updateOSPlayerId();
    }

    private void updateOSPlayerId() {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        boolean isEnabled = status.getPermissionStatus().getEnabled();
        boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();
        if (isEnabled && isSubscribed){
            String userID = status.getSubscriptionStatus().getUserId();
            if (sessionManager.getOsPlayerId() != userID){
                sessionManager.setOsPlayerId(userID);
            }
        }
    }
}
