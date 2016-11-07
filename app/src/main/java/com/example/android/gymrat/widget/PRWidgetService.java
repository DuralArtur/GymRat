package com.example.android.gymrat.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Artur on 28-Oct-16.
 */

public class PRWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new PRViewsFactory(this.getApplicationContext(),
                intent));
    }
}
