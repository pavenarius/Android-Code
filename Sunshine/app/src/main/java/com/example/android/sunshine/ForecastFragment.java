package com.example.android.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Peter Avenarius on 2/9/2015.
 */
public class ForecastFragment extends Fragment {
   private ArrayAdapter<String> mForecastAdapter;

   public ForecastFragment() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // add this line for this fragment to handle menu events
      setHasOptionsMenu(true);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      // Inflate the menu; this adds items to the action bar if it is present.
      inflater.inflate(R.menu.forecast_fragment, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_refresh) {
         // invoke worker thread to load data
         // TODO: drive location from UI input
         FetchWeatherForecastTask forecastTask = new FetchWeatherForecastTask();
         forecastTask.execute("Miami");
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      // create fake weather data ArrayList
      ArrayList<String> fakeWeatherDataList = new ArrayList<String>();
      fakeWeatherDataList.add("DAY 1");
      fakeWeatherDataList.add("DAY 2");
      fakeWeatherDataList.add("DAY 3");
      fakeWeatherDataList.add("DAY 4");
      fakeWeatherDataList.add("DAY 5");
      fakeWeatherDataList.add("DAY 6");
      fakeWeatherDataList.add("DAY 7");

      // create adapter to load weather data into list view
      mForecastAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_text_view, fakeWeatherDataList);

      // bind adapter to list view
      ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
      listView.setAdapter(mForecastAdapter);

      return rootView;
   }

   private class FetchWeatherForecastTask extends AsyncTask<String, Void, String[]> {
      private final String LOG_TAG = FetchWeatherForecastTask.class.getSimpleName();

      protected String[] doInBackground(String... locations) {
         return getWeatherJson(locations);
      }

      protected void onPostExecute(String[] forecastData) {
         if (forecastData != null) {
            mForecastAdapter.clear();
            mForecastAdapter.addAll(forecastData);
         }
      }

      private String[] getWeatherJson(String... locations) {
         // These two need to be declared outside the try/catch
         // so that they can be closed in the finally block.
         HttpURLConnection urlConnection = null;
         BufferedReader reader = null;

         // Will contain the raw JSON response as a string.
         String forecastJsonStr = null;

         // TODO: these values should come from the app settings
         String dataFormat = "json";
         String units = "imperial";
         int numDays = 7;

         try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            // TODO: drive base URL from properties file or the Android equivalent
            final String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily";
            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("q", locations[0])
                    .appendQueryParameter("mode", dataFormat)
                    .appendQueryParameter("units", units)
                    .appendQueryParameter("cnt", Integer.toString(numDays)).build();
            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "Built URL: " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
               // Nothing to do.
               return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
               // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
               // But it does make debugging a *lot* easier if you print out the completed
               // buffer for debugging.
               buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
               // Stream was empty.  No point in parsing.
               return null;
            }
            forecastJsonStr = buffer.toString();
         } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
         } finally {
            if (urlConnection != null) {
               urlConnection.disconnect();
            }
            if (reader != null) {
               try {
                  reader.close();
               } catch (final IOException e) {
                  Log.e(LOG_TAG, "Error closing stream", e);
               }
            }
         }

         Log.d(LOG_TAG, "Returned data: " + forecastJsonStr);

         // now format data using weather data parser object
         String[] formattedWeatherData = new String[0];
         try {
            formattedWeatherData = WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, numDays);
         } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing returned JSON", e);
         }

         return formattedWeatherData;
      }
   }
}
