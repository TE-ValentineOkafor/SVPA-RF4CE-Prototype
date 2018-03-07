package com.sony.svpa.rf4ceprototype.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import java.util.Arrays;
import java.util.List;

/**
 * View for showing the list of possible TV names.
 * <p>
 * These are retrieved from the string array resource R.array.tvNames
 */

public class TvNameListView extends ListView {

  private TvNameAdapter adapter;

  public TvNameListView(Context context) {
    super(context);
  }

  public TvNameListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TvNameListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public TvNameListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    adapter = new TvNameAdapter();
    setAdapter(adapter);
    setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tvName = adapter.getItem(position);
        SettingsHelper.getHelper(getContext()).setTvName(tvName);
      }
    });
  }

  private class TvNameAdapter extends BaseAdapter {

    private List<String> nameList;

    TvNameAdapter() {
      nameList = Arrays.asList(getContext().getResources().getStringArray(R.array.tvNames));
    }

    @Override
    public int getCount() {
      return nameList.size();
    }

    @Override
    public String getItem(int position) {
      return nameList.get(position);
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
      String tvName = nameList.get(position);
      TextView text1 = (TextView) view.findViewById(android.R.id.text1);
      text1.setText(tvName);
      return view;
    }
  }

}
