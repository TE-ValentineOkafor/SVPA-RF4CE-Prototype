package com.sony.svpa.rf4ceprototype.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.AlexaOnboardingFragment;
import android.support.v17.leanback.app.OnboardingPage;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.EpgAreaChangedEvent;
import com.sony.svpa.rf4ceprototype.events.EpgCountryChangedEvent;
import com.sony.svpa.rf4ceprototype.events.EpgProviderChangedEvent;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

/**
 * Class for setting up EPG defaults.
 */

public class EpgSetupFragment extends AlexaOnboardingFragment<EpgSetupFragment.EpgSetupPage> {

  /**
   * Enumeration of the pages in the setup process.
   */
  public enum EpgSetupPage implements OnboardingPage {

    SELECT_COUNTRY(R.string.epgSetup0Title, R.string.epgSetup0Description),
    SELECT_ZIP_CODE(R.string.epgSetup1Title, R.string.epgSetup1Description),
    SELECT_AREA(R.string.epgSetupAreaTitle, R.string.epgSetupAreaDescription),
    SELECT_PROVIDER(R.string.epgSetup2Title, R.string.epgSetup2Description),
    DONE(R.string.epgSetup3Title, R.string.epgSetup3Description);

    private final int titleStringId;
    private final int descriptionStringId;

    EpgSetupPage(int titleStringId, int descriptionStringId) {
      this.titleStringId = titleStringId;
      this.descriptionStringId = descriptionStringId;
    }

    public int getTitleStringId() {
      return titleStringId;
    }

    public int getDescriptionStringId() {
      return descriptionStringId;
    }
  }

  private SettingsHelper settingsHelper;


  public EpgSetupFragment() {
    super(Arrays.asList(EpgSetupPage.values()));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    settingsHelper = SettingsHelper.getHelper(getActivity());
    getStartButton().setText(R.string.done);

  }

  @Override
  public void onResume() {
    super.onResume();
    onPageChanged(EpgSetupPage.SELECT_ZIP_CODE, EpgSetupPage.SELECT_ZIP_CODE);
  }

  @Override
  protected CharSequence getPageTitle(EpgSetupPage page) {
    return getString(page.getTitleStringId());
  }

  @Override
  protected CharSequence getPageDescription(EpgSetupPage page) {
    return getString(page.getDescriptionStringId());
  }

  @Override
  protected void onPageChanged(EpgSetupPage newPage, EpgSetupPage previousPage) {
    switch (newPage) {
      case SELECT_COUNTRY:
        getPagingIndicator().setEnabled(false);
        View countryList = View.inflate(getActivity(), R.layout.epg_country_list, null);
        animateNewContent(countryList, new FrameLayout.LayoutParams(700, 400, Gravity.CENTER), null);
        break;
      case SELECT_ZIP_CODE:
        getPagingIndicator().setEnabled(false);
        final EditText zipCodeField = (EditText) View.inflate(getActivity(), R.layout.zip_code_text, null);
        zipCodeField.setInputType(settingsHelper.getEpgCountry().zipContainsLetters() ? InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS : InputType.TYPE_CLASS_NUMBER);
        zipCodeField.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (v instanceof EditText) {
              EditText editText = (EditText) v;
              if (editText.getText().length() > 0) {
                String zip = zipCodeField.getText().toString().toUpperCase();
                zip = zip.replaceAll("[^A-Z0-9]","");
                settingsHelper.setEpgZipCode(zip);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(zipCodeField.getWindowToken(), 0);
                moveToPage(EpgSetupPage.SELECT_PROVIDER);
              }
            }
          }
        });
        animateNewContent(
            zipCodeField,
            new FrameLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER),
            new Runnable() {
              @Override
              public void run() {
                zipCodeField.requestFocus();
                if (settingsHelper.getEpgCountry().zipContainsLetters() == true) {
                  InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.showSoftInput(zipCodeField, InputMethodManager.SHOW_IMPLICIT);
                }
              }
            }
        );
        break;
      case SELECT_AREA:
        getPagingIndicator().setEnabled(false);
        View areaList = View.inflate(getActivity(), R.layout.epg_area_list, null);
        animateNewContent(areaList, new FrameLayout.LayoutParams(700, 400, Gravity.CENTER), null);
        break;
      case SELECT_PROVIDER:
        getPagingIndicator().setEnabled(false);
        View providerList = View.inflate(getActivity(), R.layout.epg_provider_list, null);
        animateNewContent(providerList, new FrameLayout.LayoutParams(700, 400, Gravity.CENTER), null);
        break;
      case DONE:
        getPagingIndicator().setEnabled(true);
        animateNewContent(null, null, null);
        break;
    }
  }

  @Subscribe
  public void onEpgCountryChanged(EpgCountryChangedEvent event) {
    List<EpgSetupPage> pages = Arrays.asList(EpgSetupPage.values());
    switch (event.getCountry().getProviderSearchType()) {
      case ZIP_CODE:
        moveToPage(EpgSetupPage.SELECT_ZIP_CODE);
        break;
      case AREA:
        moveToPage(EpgSetupPage.SELECT_AREA);
        break;
      case NONE:
        moveToPage(EpgSetupPage.SELECT_PROVIDER);
        break;
      default:
        break;
    }
  }

  @Subscribe
  public void onEpgAreaChanged(EpgAreaChangedEvent event) {
    moveToPage(EpgSetupPage.SELECT_PROVIDER);
  }

  @Subscribe
  public void onEpgProviderChanged(EpgProviderChangedEvent event) {
    getActivity().setResult(Activity.RESULT_OK);
    moveToNextPage();
  }

  @Override
  protected void onFinishFragment() {
    super.onFinishFragment();
    getActivity().finish();
  }
}
