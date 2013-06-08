package beit.skn.pingmeuser;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class PingMeWidgetProvider extends AppWidgetProvider 
{
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Intent intent = new Intent(context, DashboardActivity.class);
		intent.setAction(UserApplication.INTENT_FROM_WIDGET);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		views.setOnClickPendingIntent(R.id.pingMeVoice, pendIntent);
		appWidgetManager.updateAppWidget(appWidgetIds,views);      
	}

}
