package com.sony.svpa.rf4ceprototype.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.sony.svpa.rf4ceprototype.epg.EpgServerApi;
import com.sony.svpa.rf4ceprototype.models.EpgArea;
import com.sony.svpa.rf4ceprototype.models.EpgCountry;
import com.sony.svpa.rf4ceprototype.models.Provider;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for showing the list of EPG providers
 * <p>
 * These are retrieved from the CSX server.
 */

public class EpgProviderListView extends FrameLayout {

  private ProviderListAdapter adapter;

  public EpgProviderListView(Context context) {
    super(context);
  }

  public EpgProviderListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EpgProviderListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public EpgProviderListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    findProviders();
  }

  void findProviders() {
    final EpgCountry country = SettingsHelper.getHelper(getContext()).getEpgCountry();
    final EpgServerApi epg = EpgServerApi.getInstance(getContext());
    switch (country.getProviderSearchType()) {
      case ZIP_CODE:
        final String zipCode = SettingsHelper.getHelper(getContext()).getEpgZipCode();
        if (zipCode == null) {
          return;
        }
        new AsyncTask<Void, Void, List<Provider>>() {

          @Override
          protected List<Provider> doInBackground(Void... params) {
            return epg.getProviderList(country, zipCode);
          }

          @Override
          protected void onPostExecute(List<Provider> providers) {
            super.onPostExecute(providers);
            showProviders(providers);
          }
        }.execute();
        break;
      case AREA:
        final EpgArea area = SettingsHelper.getHelper(getContext()).getEpgArea();
        if (area == null) {
          return;
        }
        new AsyncTask<Void, Void, List<Provider>>() {

          @Override
          protected List<Provider> doInBackground(Void... params) {
            return epg.getProviderList(area);
          }

          @Override
          protected void onPostExecute(List<Provider> providers) {
            super.onPostExecute(providers);
            showProviders(providers);
          }
        }.execute();
        break;
      case NONE:
        new AsyncTask<Void, Void, List<Provider>>() {

          @Override
          protected List<Provider> doInBackground(Void... params) {
            return epg.getProviderList(country);
          }

          @Override
          protected void onPostExecute(List<Provider> providers) {
            super.onPostExecute(providers);
            showProviders(providers);
          }
        }.execute();
        break;
    }
  }

  void showProviders(List<Provider> providers) {
    if (providers == null) {
      new AlertDialog.Builder(getContext())
          .setTitle(R.string.error)
          .setMessage(R.string.noProvidersFound)
          .setPositiveButton(R.string.quit, null)
          .setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
              //EventBus.getDefault().post(new FinishActivityEvent(ProviderActivity.class));
            }
          })
          .show();
      return;
    }
    adapter = new ProviderListAdapter(providers);
    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Provider provider = adapter.getItem(position);
        SettingsHelper.getHelper(getContext()).setEpgProvider(provider);
      }
    });
    list.setVisibility(View.VISIBLE);
    spinner.setVisibility(View.GONE);
    list.requestFocus();
  }

  private class ProviderListAdapter extends BaseAdapter {

    private final List<Provider> providerList;

    private ProviderListAdapter(List<Provider> values) {
      providerList = new ArrayList<>(values);
    }


    @Override
    public int getCount() {
      return providerList.size();
    }

    @Override
    public Provider getItem(int position) {
      return providerList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
      if (view == null) {
        view = View.inflate(getContext(), R.layout.tv_name_cell, null);
      }
      String name = providerList.get(position).getName();
      TextView text1 = (TextView) view.findViewById(android.R.id.text1);
      text1.setText(name);
      return view;
    }
  }

}
