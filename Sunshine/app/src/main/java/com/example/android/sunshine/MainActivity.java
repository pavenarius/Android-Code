package com.example.android.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      if (savedInstanceState == null) {
         getSupportFragmentManager().beginTransaction()
                 .add(R.id.container, new PlaceholderFragment())
                 .commit();
      }
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment {

      public PlaceholderFragment() {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_main, container, false);

         // create fake weather data ArrayList
         // TODO: populate this with dynamic real data

         ArrayList<String> fakeWeatherDataList = new ArrayList<String>();
         fakeWeatherDataList.add("Today - Sunny - 75/55");
         fakeWeatherDataList.add("Tomorrow - Rainy - 80/75");
         fakeWeatherDataList.add("Sunday - Party Cloudy - 88/63");
         fakeWeatherDataList.add("Monday - Sunny - 80/72");
         fakeWeatherDataList.add("Tuesday - Sunny - 80/72");
         fakeWeatherDataList.add("Wednesday - Rainy - 70/55");
         fakeWeatherDataList.add("Thursday - Rainy - 72/58");

         // create adapter to load weather data into list view
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_text_view, fakeWeatherDataList);

         // bind adapter to list view
         ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
         listView.setAdapter(arrayAdapter);

         return rootView;
      }
   }
}
