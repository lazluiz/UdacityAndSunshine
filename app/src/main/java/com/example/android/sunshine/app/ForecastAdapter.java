package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.utils.ArtUtils;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());

        int layoutId =
                viewType == VIEW_TYPE_FUTURE_DAY ? R.layout.list_item_forecast :
                viewType == VIEW_TYPE_TODAY ? R.layout.list_item_forecast_today : -1;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int cWeatherCondId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        long cWeatherDate = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String cWeatherDesc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        double cWeatherMaxTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double cWeatherMinTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);

        // Weather Icon
        int viewType = getItemViewType(cursor.getPosition());
        if(viewType == VIEW_TYPE_FUTURE_DAY){
            viewHolder.iconView.setImageResource(ArtUtils.getIconResourceForWeatherCondition(cWeatherCondId));
        }else if(viewType == VIEW_TYPE_TODAY){
            viewHolder.iconView.setImageResource(ArtUtils.getArtResourceForWeatherCondition(cWeatherCondId));
        }

        // Weather Date
        viewHolder.dateView.setText(Utility.getFriendlyDayString(mContext, cWeatherDate));

        // Weather Description
        viewHolder.descriptionView.setText(cWeatherDesc);

        // Weather Temperatures
        boolean isMetric = Utility.isMetric(mContext);
        viewHolder.highTempView.setText(Utility.formatTemperature(mContext, cWeatherMaxTemp, isMetric));
        viewHolder.lowTempView.setText(Utility.formatTemperature(mContext, cWeatherMinTemp, isMetric));
    }
}