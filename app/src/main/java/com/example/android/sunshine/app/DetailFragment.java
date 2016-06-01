package com.example.android.sunshine.app;

/**
 * Created by Luiz F. Lazzarin on 12/05/2016.
 * Email: lf.lazzarin@gmail.com
 * Github: /luizfelippe
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import com.example.android.sunshine.app.custom.CompassView;
import com.example.android.sunshine.app.data.WeatherContract;

import java.util.Locale;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private String mForecast = "";
    private Uri mUri;

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

    private ImageView mIconView;
    private TextView mDateView;
    private TextView mDayView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private CompassView mCompassView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_desc_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_temperature_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_temperature_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mCompassView = (CompassView) rootView.findViewById(R.id.detail_compass);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);
    }

    void onLocationSettingChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
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
        return mUri == null ? null : new CursorLoader(
                getActivity(),
                mUri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Check if there's data
        if (data != null && data.moveToFirst()) {

            int cWeatherCondId = data.getInt(COL_WEATHER_CONDITION_ID);
            long cWeatherDate = data.getLong(COL_WEATHER_DATE);
            String cWeatherDesc = data.getString(COL_WEATHER_DESC);
            double cWeatherMaxTemp = data.getDouble(COL_WEATHER_MAX_TEMP);
            double cWeatherMinTemp = data.getDouble(COL_WEATHER_MIN_TEMP);
            float cWeatherHumidity = data.getFloat(COL_WEATHER_HUMIDITY);
            float cWeatherWindSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
            float cWeatherWindDirection = data.getFloat(COL_WEATHER_DEGREES);
            float cWeatherPressure = data.getFloat(COL_WEATHER_PRESSURE);

            // Weather Date
            mDayView.setText(Utility.Format.getDayName(getActivity(), cWeatherDate));
            mDateView.setText(Utility.Format.getFormattedMonthDay(getActivity(), cWeatherDate));

            // Weather Temperatures
            Context c = getActivity();
            boolean isMetric = Utility.Settings.isMetric(c);
            mHighTempView.setText(Utility.Format.formatTemperature(c, cWeatherMaxTemp, isMetric));
            mLowTempView.setText(Utility.Format.formatTemperature(c, cWeatherMinTemp, isMetric));

            // Weather Image
            mIconView.setImageResource(Utility.Art.getArtResourceForWeatherCondition(cWeatherCondId));
            mIconView.setContentDescription(cWeatherDesc);

            // Weather Description
            mDescriptionView.setText(cWeatherDesc);

            // Weather Details
            mHumidityView.setText(Utility.Format.getFormattedHumidity(c, cWeatherHumidity));
            mWindView.setText(Utility.Format.getFormattedWind(c, cWeatherWindSpeed, cWeatherWindDirection));
            mPressureView.setText(Utility.Format.getFormattedPressure(c, cWeatherPressure));

            // Weather Compass
            mCompassView.setDirection(cWeatherWindDirection);

            // Sharing
            mForecast = String.format(Locale.getDefault(), "%s - %s - %s/%s",
                    mDateView.getText(),
                    mDescriptionView.getText(),
                    mHighTempView.getText(),
                    mLowTempView.getText());

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
}
