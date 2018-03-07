package com.sony.svpa.rf4ceprototype.epg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sony.svpa.rf4ceprototype.models.Airing;
import com.sony.svpa.rf4ceprototype.models.Channel;
import com.sony.svpa.rf4ceprototype.models.EpgArea;
import com.sony.svpa.rf4ceprototype.models.EpgCountry;
import com.sony.svpa.rf4ceprototype.models.Language;
import com.sony.svpa.rf4ceprototype.models.Provider;
import com.sony.svpa.rf4ceprototype.models.VideoProvider;
import com.sony.svpa.rf4ceprototype.models.VideoSearchResult;
import com.sony.svpa.rf4ceprototype.utils.UtcDateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface to EPG data.
 */
public class EpgServerApi {

  private static final String TAG = EpgServerApi.class.getSimpleName();

  private static final String API_BASE_URL = "https://api-deva.meta.csxdev.com";
  private static final String COUNTRY = "USA";
  private static final String API_KEY = "B0cjG5P7T5KNnwig0nBIr00WxbSl5l0gIvK5TSfOdlab50Gv2sIDNJ300000000Zae1PMqfVYpqS600yBulbymjst";

  private static EpgServerApi INSTANCE;

  private final MetafrontService service;

  public static EpgServerApi getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new EpgServerApi(context.getApplicationContext());
    }
    return INSTANCE;
  }

  /**
   * Gson wrapper for basic metafront list reponse.
   *
   * @param <T> Type of items in list returned by metafront.
   */
  private static class MetafrontListResponse<T> {

    private List<T> items;

    public List<T> getItems() {
      return items;
    }
  }

  /**
   * Gson wrapper for Array embedded in response.
   *
   * @param <T> Type of items in list returned by metafront.
   */
  private static class EpgArray<T> {

    private String domain;
    private List<T> items;

    public List<T> getItems() {
      return items;
    }
  }

  /**
   * Gson wrapper for metafront EPG query response.
   *
   * @param <T> Type of items in list returned by metafront.
   */
  private static class MetafrontEpgResponse<T> {

    private Map<String, EpgArray<T>> items;

    public Map<String, EpgArray<T>> getItems() {
      return items;
    }
  }

  /**
   * Declaration of the REST service API.
   */
  private interface MetafrontService {

    /**
     * Get the list of channels for a given zip code.
     *
     * @param zipCode Zip code.
     * @return List of providers.
     */
    @GET("/v2/rest/tv/country/{country}/zipcode/{zipCode}/provider.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontListResponse<Provider>> getProviderListForZipCode(
            @Path("country") EpgCountry country,
            @Path("zipCode") String zipCode
    );

    /**
     * Get the list of channels for a given provider.
     *
     * @param providerId Provider ID.
     * @return List of channels.
     */
    @GET("/v2/rest/tv/provider/{providerId}/channel.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontListResponse<Channel>> getChannelList(@Path("providerId") String providerId);

    /**
     * Retrieve EPG data for programs currently airing.
     *
     * @param channelIds Comma delimited list of channels.
     * @return Airing data for what is now playing on specified channels.
     */
    @GET("/v2/service/tv/grid_on_the_air.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontEpgResponse<Airing>> getEpgData(@Query("channels") String channelIds);

    /**
     * Retrieve EPG data for a specified set of channels, start time and duration.
     *
     * @param channelIds  Comma-delimited list of channel IDs. The practical size limit for this is around 200 channels before the URL becomes too long.
     * @param startTime   Start time in UTC format yyyy-MM-ddTHH:mm:ssZ(UTC)(Restriction: "ss" must be "00", "mm" must be multiples of 5)
     * @param duration    Duration time specified integer value in multiples of 300 seconds(e.g. "3600").(default: 21600)
     * @param description Description ON/OFF(true/false)
     * @return Airing data for the specified channel(s) and time range.
     */
    @GET("/v2/service/tv/grid.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontEpgResponse<Airing>> getEpgData(
            @Query("channels") String channelIds,
            @Query(value = "starttime", encoded = true) String startTime,
            @Query("duration") int duration,
            @Query("description") boolean description
    );


    @GET("/v2/service/{provider}/search.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontListResponse<VideoSearchResult>> searchVideo(
            @Path("provider") VideoProvider provider,
            @Query("text") String searchText,
            @Query("language") Language language,
            @Query("country") EpgCountry country
    );

    @GET("/v2/rest/tv/country/{country}/provider.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontListResponse<Provider>> getProviderListForCountry(
            @Path("country") EpgCountry country
    );

    @GET("/v2/rest/tv/area/{area}/provider.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontListResponse<Provider>> getProviderListForArea(
            @Path("area") String areaId
    );

    @GET("/v2/rest/tv/country/{country}/area.json")
    @Headers("X-API-Key:" + API_KEY)
    Call<MetafrontListResponse<EpgArea>> getAreaList(
            @Path("country") EpgCountry country
    );
  }

  private EpgServerApi(Context context) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    service = retrofit.create(MetafrontService.class);
  }

  /**
   * Get the list of providers for a zip code.
   * NOTE: This method is synchronous and should not be called on the UI thread.
   *
   * @param zipCode Client zip code.
   * @return List of providers, or null if an error occurs.
   */
  public List<Provider> getProviderList(@NonNull EpgCountry country, @NonNull String zipCode) {
    Call<MetafrontListResponse<Provider>> call = service.getProviderListForZipCode(country, zipCode);
    try {
      Response<MetafrontListResponse<Provider>> response = call.execute();
      if (response.isSuccessful()) {
        if (response.body().getItems() != null) {
          // items were found
          return response.body().getItems();
        } else {
          // no items found, return empty list
          return new ArrayList<>();
        }
      } else {
        java.util.Scanner s = new java.util.Scanner(response.errorBody().byteStream()).useDelimiter("\\A");
        String error = s.hasNext() ? s.next() : "";
        Log.e(TAG, "Error retrieving provider list: " + error);
        return null;
      }
    } catch (IOException e) {
      Log.e(TAG, "Error retrieving provider list:" + e + ".");
      return null;
    }
  }

  /**
   * Get the list of channels for a provider.
   * NOTE: This method is synchronous and should not be called on the UI thread.
   *
   * @param providerId Provider ID.
   * @return List of channels, or null if an error occurs.
   */
  public List<Channel> getChannelList(@NonNull String providerId) {
    Call<MetafrontListResponse<Channel>> call = service.getChannelList(providerId);
    try {
      Response<MetafrontListResponse<Channel>> response = call.execute();
      if (response.isSuccessful()) {
        if (response.body().getItems() != null) {
          // items were found
          Log.d(TAG, "Found " + response.body().getItems().size() + " channels.");
          String callUrl = call.request().toString();
          Log.d(TAG, "Channel Query Url: " + callUrl);
          return response.body().getItems();
        } else {
          // no items found, return empty list
          return new ArrayList<>();
        }
      } else {
        java.util.Scanner s = new java.util.Scanner(response.errorBody().byteStream()).useDelimiter("\\A");
        String error = s.hasNext() ? s.next() : "";
        Log.e(TAG, "Error retrieving channel list: " + error);
        return null;
      }
    } catch (IOException e) {
      Log.e(TAG, "Error retrieving channel list:" + e + ".");
      return null;
    }
  }

  /**
   * Get EPG data for a given channel, start time, and time span.
   * NOTE: This method is synchronous and should not be called on the UI thread.
   *
   * @param channelId     Single channel ID to query.
   * @param startDateTime Start date/time in local time.
   * @param duration      Number of seconds of EPG data to retrieve, in units of 300 (5 min)
   * @return EPG data for the requested channel and time range.
   */
  public List<Airing> getPrograms(
      @NonNull String channelId,
      @NonNull Date startDateTime,
      int duration
  ) {
    final String dateString = UtcDateUtil.getUtcDateTime(startDateTime, true);
    // round duration to a 5-minute interval
    duration = (duration / 300) * 300;
    Log.d(TAG, "Retrieving EPG data: channel = " + channelId + ", startTime = " + dateString + ", duration = " + duration + ".");
    Call<MetafrontEpgResponse<Airing>> call = service.getEpgData(channelId, dateString, duration, true);
    try {
      Response<MetafrontEpgResponse<Airing>> response = call.execute();
      if (response.isSuccessful()) {
        if (response.body().getItems().get(channelId) != null) {
          // items were found
          return response.body().getItems().get(channelId).getItems();
        } else {
          // no items found, return empty list
          return new ArrayList<>();
        }
      } else {
        java.util.Scanner s = new java.util.Scanner(response.errorBody().byteStream()).useDelimiter("\\A");
        String error = s.hasNext() ? s.next() : "";
        Log.e(TAG, "Error retrieving EPG data: " + error);
        return null;
      }
    } catch (IOException e) {
      Log.e(TAG, "Error retrieving EPG data:" + e + ".");
      return null;
    }
  }

  /**
   * Search for videos from a third party service.
   *
   * @param searchText    Text to search for.
   * @param videoProvider Search provider.
   * @return A list of matching video search results (top 10 is default)
   */
  public List<VideoSearchResult> searchVideo(
      @NonNull String searchText,
      @NonNull VideoProvider videoProvider
  ) {
    Log.d(TAG, "Searching video: provider = " + videoProvider + ", text = " + searchText + ".");
    Call<MetafrontListResponse<VideoSearchResult>> call = service.searchVideo(videoProvider, searchText, Language.ENGLISH, EpgCountry.USA);
    try {
      Response<MetafrontListResponse<VideoSearchResult>> response = call.execute();
      return response.body().getItems();
    } catch (IOException e) {
      Log.e(TAG, "Error performing video search:" + e + ".");
      return null;
    }
  }

  public List<Provider> getProviderList(@NonNull EpgCountry country) {
    Log.d(TAG, "Searching providers: country = " + country + ".");
    Call<MetafrontListResponse<Provider>> call = service.getProviderListForCountry(country);
    try {
      Response<MetafrontListResponse<Provider>> response = call.execute();
      return response.body().getItems();
    } catch (IOException e) {
      Log.e(TAG, "Error getting provider list:" + e + ".");
      return null;
    }
  }

  public List<EpgArea> getAreaList(@NonNull EpgCountry country) {
    Log.d(TAG, "Searching areas for: country = " + country + ".");
    Call<MetafrontListResponse<EpgArea>> call = service.getAreaList(country);
    try {
      Response<MetafrontListResponse<EpgArea>> response = call.execute();
      return response.body().getItems();
    } catch (IOException e) {
      Log.e(TAG, "Error getting area list:" + e + ".");
      return null;
    }
  }

  public List<Provider> getProviderList(@NonNull EpgArea area) {
    Log.d(TAG, "Searching providers: area = " + area.getName() + ".");
    Call<MetafrontListResponse<Provider>> call = service.getProviderListForArea(area.getId());
    try {
      Response<MetafrontListResponse<Provider>> response = call.execute();
      return response.body().getItems();
    } catch (IOException e) {
      Log.e(TAG, "Error getting provider list:" + e + ".");
      return null;
    }
  }

}
