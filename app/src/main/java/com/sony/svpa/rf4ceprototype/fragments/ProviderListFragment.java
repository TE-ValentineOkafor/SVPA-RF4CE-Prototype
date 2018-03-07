package com.sony.svpa.rf4ceprototype.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.epg.EpgServerApi;
import com.sony.svpa.rf4ceprototype.events.EpgProviderChangedEvent;
import com.sony.svpa.rf4ceprototype.models.EpgArea;
import com.sony.svpa.rf4ceprototype.models.EpgCountry;
import com.sony.svpa.rf4ceprototype.models.Provider;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderListFragment extends Fragment {
    private View rootView;
    private ListView providerListView;
    private ProgressBar progressBar;
    private ProviderListAdapter adapter;


    public ProviderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_provider_list, container, false);
        providerListView = rootView.findViewById(R.id.list_view_provider);
        progressBar = rootView.findViewById(R.id.spinner);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        providerListView.setVisibility(View.GONE);
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
                            //EventBus.getInstance().post(new FinishActivityEvent(EpgSetupActivity.class));
                        }
                    })
                    .show();
            return;
        }
        adapter = new ProviderListAdapter(providers);
        providerListView.setAdapter(adapter);
        providerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Provider provider = adapter.getItem(position);
                SettingsHelper.getHelper(getContext()).setEpgProvider(provider);
                EventBus.getDefault().post(new EpgProviderChangedEvent(provider));
            }
        });
        providerListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        providerListView.requestFocus();
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
