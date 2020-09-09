package cc.shinichi.library.view.photoview;

import android.support.annotation.VisibleForTesting;

public abstract class Factory {

    // Making this volatile because on the unit tests, setInstance is called from a unit test
    // thread, and then it's read on the UI thread.
    private static volatile Factory sInstance;
    @VisibleForTesting
    protected static boolean sRegistered;
    @VisibleForTesting
    protected static boolean sInitialized;

    public static Factory get() {
        return sInstance;
    }

    protected static void setInstance(final Factory factory) {
        sInstance = factory;
    }
    public abstract void onRequiredPermissionsAcquired();

    public abstract void onActivityResume();
}
