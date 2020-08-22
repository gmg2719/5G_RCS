package per.goweii.burred;

import android.graphics.Bitmap;
import android.view.View;

public class DefaultSnapshotInterceptor implements Blurred.SnapshotInterceptor {
    @Override
    public Bitmap snapshot(View from, int backgroundColor, int foregroundColor, float scale, boolean antiAlias) {
        return BitmapProcessor.get().snapshot(from, backgroundColor, foregroundColor, scale, antiAlias);
    }
}
