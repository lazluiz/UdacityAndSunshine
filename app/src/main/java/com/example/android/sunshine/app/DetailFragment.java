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
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    // Just some boilerplate code from Google
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;

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
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        View view = getView();
        ViewHolder viewHolder;

        if (!data.moveToFirst() || view == null) return;

        if (view.getTag() == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) view.getTag();

        viewHolder.txtDay.setText(Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE)));
        viewHolder.txtDate.setText(Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE)));

        Context c = getActivity();
        boolean isMetric = Utility.isMetric(c);
        viewHolder.txtHighTemp.setText(Utility.formatTemperature(c, data.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
        viewHolder.txtLowTemp.setText(Utility.formatTemperature(c, data.getDouble(COL_WEATHER_MIN_TEMP), isMetric));

        viewHolder.imgForecast.setImageResource(R.mipmap.ic_launcher);
        viewHolder.txtDescription.setText(data.getString(COL_WEATHER_DESC));

        viewHolder.txtHumidity.setText(Utility.getFormattedHumidity(c, data.getFloat(COL_WEATHER_HUMIDITY)));
        viewHolder.txtWind.setText(Utility.getFormattedWind(c, data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES)));
        viewHolder.txtPressure.setText(Utility.getFormattedPressure(c, data.getFloat(COL_WEATHER_PRESSURE)));

//            String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
//            String weatherDesc = data.getString(COL_WEATHER_DESC);
//
//            boolean isMetric = Utility.isMetric(getActivity());
//
//            String highTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
//            String lowTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

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
