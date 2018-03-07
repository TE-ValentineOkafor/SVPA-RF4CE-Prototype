package com.sony.svpa.rf4ceprototype.view;

import android.content.Context;
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
import com.sony.svpa.rf4ceprototype.models.EpgArea;
import com.sony.svpa.rf4ceprototype.models.EpgCountry;
import com.sony.svpa.rf4ceprototype.models.ProviderSearchType;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for showing the list of EPG areas for countries that support it.
 * <p>
 * These are retrieved from the CSX server.
 */

public class EpgAreaListView extends FrameLayout {

  private AreaListAdapter adapter;

  public EpgAreaListView(Context context) {
    super(context);
  }

  public EpgAreaListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EpgAreaListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public EpgAreaListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    findAreas();
  }

  void findAreas() {
    final EpgCountry country = SettingsHelper.getHelper(getContext()).getEpgCountry();
    //final EpgServerApi epg = EpgServerApi.getInstance(getContext());
    if (country.getProviderSearchType() != ProviderSearchType.AREA) {
      return;
    }
    new AsyncTask<Void, Void, List<EpgArea>>() {

      @Override
      protected List<EpgArea> doInBackground(Void... params) {
      //  return epg.getAreaList(country);
        return null;
      }

      @Override
      protected void onPostExecute(List<EpgArea> areas) {
        super.onPostExecute(areas);
        showAreas(areas);
      }
    }.execute();


  }

  void showAreas(List<EpgArea> areas) {
    adapter = new AreaListAdapter(areas);
    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EpgArea area = adapter.getItem(position);
        SettingsHelper.getHelper(getContext()).setEpgArea(area);
      }
    });
    list.setVisibility(View.VISIBLE);
    spinner.setVisibility(View.GONE);
    list.requestFocus();
  }

  private class AreaListAdapter extends BaseAdapter {

    private final List<EpgArea> areaList;

    private AreaListAdapter(List<EpgArea> values) {
      areaList = new ArrayList<>(values);
    }


    @Override
    public int getCount() {
      return areaList.size();
    }

    @Override
    public EpgArea getItem(int position) {
      return areaList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
      if (view == null) {
        view = View.inflate(getContext(), R.layout.epg_area_cell, null);
      }
      String name = areaList.get(position).getName();
      TextView text1 = (TextView) view.findViewById(android.R.id.text1);
      text1.setText(name);
      return view;
    }
  }

}
