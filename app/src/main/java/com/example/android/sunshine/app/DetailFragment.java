package com.example.android.sunshine.app;

/**
 * Created by Sunshine-Version-2-1.01_hello_world on 12/05/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.utils.ArtUtils;

import java.util.Locale;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    ShareActionProvider mShareActionProvider;
    private String mForecast = "";

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    // Just some boilerplate code from Google
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        return new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, mForecast.concat(FORECAST_SHARE_HASHTAG));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        return intent != null ? new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        ) : null;
    }

    public static class ViewHolder {
        public final TextView txtDay;
        public final TextView txtDate;
        public final TextView txtHighTemp;
        public final TextView txtLowTemp;
        public final ImageView imgForecast;
        public final TextView txtDescription;
        public final TextView txtHumidity;
        public final TextView txtWind;
        public final TextView txtPressure;

        public ViewHolder(View view) {
            txtDay = (TextView) view.findViewById(R.id.day_textview);
            txtDate = (TextView) view.findViewById(R.id.date_textview);
            txtHighTemp = (TextView) view.findViewById(R.id.high_temperature_textview);
            txtLowTemp = (TextView) view.findViewById(R.id.low_temperature_textview);
            imgForecast = (ImageView) view.findViewById(R.id.forecast_icon);
            txtDescription = (TextView) view.findViewById(R.id.desc_textview);
            txtHumidity = (TextView) view.findViewById(R.id.humidity_textview);
            txtWind = (TextView) view.findViewById(R.id.wind_textview);
            txtPressure = (TextView) view.findViewById(R.id.pressure_textview);
        }

        public static ViewHolder getInstance(View view) {

            if (view.getTag() == null) {
                view.setTag(new ViewHolder(view));
            }

            return (ViewHolder) view.getTag();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        View view = getView();

        // Check if there's data and make sure the view is not null - who knows?
        if (data == null || !data.moveToFirst() || view == null) return;

        ViewHolder viewHolder = ViewHolder.getInstance(view);

        int cWeatherCondId = data.getInt(COL_WEATHER_CONDITION_ID);
        long cWeatherDate = data.getLong(COL_WEATHER_DATE);
        String cWeatherDesc = data.getString(COL_WEATHER_DESC);
        double cWeatherMaxTemp = data.getDouble(COL_WEATHER_MAX_TEMP);
        double cWeatherMinTemp = data.getDouble(COL_WEATHER_MIN_TEMP);
        float cWeatherHumidity =  data.getFloat(COL_WEATHER_HUMIDITY);
        float cWeatherWindSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float cWeatherWindDirection = data.getFloat(COL_WEATHER_DEGREES);
        float cWeatherPressure = data.getFloat(COL_WEATHER_PRESSURE);

        // Weather Date
        viewHolder.txtDay.setText(Utility.getDayName(getActivity(), cWeatherDate));
        viewHolder.txtDate.setText(Utility.getFormattedMonthDay(getActivity(), cWeatherDate));

        // Weather Temperatures
        Context c = getActivity();
        boolean isMetric = Utility.isMetric(c);
        viewHolder.txtHighTemp.setText(Utility.formatTemperature(c, cWeatherMaxTemp, isMetric));
        viewHolder.txtLowTemp.setText(Utility.formatTemperature(c, cWeatherMinTemp, isMetric));

        // Weather Image
        viewHolder.imgForecast.setImageResource(ArtUtils.getArtResourceForWeatherCondition(cWeatherCondId));

        // Weather Description
        viewHolder.txtDescription.setText(cWeatherDesc);

        // Weather Details
        viewHolder.txtHumidity.setText(Utility.getFormattedHumidity(c, cWeatherHumidity));
        viewHolder.txtWind.setText(Utility.getFormattedWind(c, cWeatherWindSpeed, cWeatherWindDirection));
        viewHolder.txtPressure.setText(Utility.getFormattedPressure(c, cWeatherPressure));

        // Sharing
        mForecast = String.format(Locale.getDefault(), "%s - %s - %s/%s",
                viewHolder.txtDay.getText(),
                viewHolder.txtDescription.getText(),
                viewHolder.txtHighTemp.getText(),
                viewHolder.txtLowTemp.getText());

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
