package com.sony.svpa.rf4ceprototype.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.models.EpgCountry;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for showing the list of EPG countries
 * <p>
 * These are currently in the {@link EpgCountry} enum, but later may be server based.
 */

public class EpgCountryListView extends FrameLayout {

  private CountryListAdapter adapter;

  public EpgCountryListView(Context context) {
    super(context);
  }

  public EpgCountryListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EpgCountryListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public EpgCountryListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @BindView(android.R.id.list)
  ListView list;

  @BindView(R.id.spinner)
  ProgressBar spinner;

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
    list.setVisibility(View.GONE);
    findCountries();
  }

  void findCountries() {
    // TODO possibly retrieve from server later
    showCountries(Arrays.asList(EpgCountry.values()));
  }

  void showCountries(List<EpgCountry> countries) {
    adapter = new CountryListAdapter(countries);
    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EpgCountry country = adapter.getItem(position);
        SettingsHelper.getHelper(getContext()).setEpgCountry(country);
      }
    });
    list.setVisibility(View.VISIBLE);
    spinner.setVisibility(View.GONE);
    list.requestFocus();
  }

  private class CountryListAdapter extends BaseAdapter {

    private final List<EpgCountry> countryList;

    private CountryListAdapter(List<EpgCountry> values) {
      countryList = new ArrayList<>(values);
    }


    @Override
    public int getCount() {
      return countryList.size();
    }

    @Override
    public EpgCountry getItem(int position) {
      return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
      if (view == null) {
        view = View.inflate(getContext(), R.layout.country_cell, null);
      }
      String name = countryList.get(position).getDisplayName();
      TextView text1 = (TextView) view.findViewById(android.R.id.text1);
      text1.setText(name);
      return view;
    }
  }

}
