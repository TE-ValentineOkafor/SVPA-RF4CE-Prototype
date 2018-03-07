package com.sony.svpa.rf4ceprototype.models;

import java.util.Locale;

/**
 * Enum of countries for CSX and EPG configuration UI
 * <p>
 * NOTE enum name must match CSX 3 letter country code.
 */

public enum EpgCountry {

  // special case countries
  USA(Locale.US, ProviderSearchType.ZIP_CODE, false),
  CAN(Locale.CANADA, ProviderSearchType.ZIP_CODE, true),
  MEX(new Locale("", "MX"), ProviderSearchType.ZIP_CODE, false),
  JPN(Locale.JAPAN, ProviderSearchType.NONE),

  // area based countries
  ARE(new Locale("", "AE"), ProviderSearchType.AREA),
  ARG(new Locale("", "AR"), ProviderSearchType.AREA),
  AUS(new Locale("", "AU"), ProviderSearchType.AREA),
  AUT(new Locale("", "AT"), ProviderSearchType.AREA),
  BEL(new Locale("", "BE"), ProviderSearchType.AREA),
  BGR(new Locale("", "BG"), ProviderSearchType.AREA),
  BHR(new Locale("", "BH"), ProviderSearchType.AREA),
  BRA(new Locale("", "BR"), ProviderSearchType.AREA),
  CHE(new Locale("", "CH"), ProviderSearchType.AREA),
  CHN(new Locale("", "CN"), ProviderSearchType.AREA),
  CZE(new Locale("", "CZ"), ProviderSearchType.AREA),
  DEU(Locale.GERMANY, ProviderSearchType.AREA),
  DNK(new Locale("", "DK"), ProviderSearchType.AREA),
  ESP(new Locale("", "ES"), ProviderSearchType.AREA),
  EST(new Locale("", "EE"), ProviderSearchType.AREA),
  FIN(new Locale("", "FI"), ProviderSearchType.AREA),
  FRA(Locale.FRANCE, ProviderSearchType.AREA),
  GBR(Locale.UK, ProviderSearchType.AREA),
  HRV(new Locale("", "HR"), ProviderSearchType.AREA),
  IDN(new Locale("", "ID"), ProviderSearchType.AREA),
  IND(new Locale("", "IN"), ProviderSearchType.AREA),
  IRL(new Locale("", "IE"), ProviderSearchType.AREA),
  ITA(Locale.ITALY, ProviderSearchType.AREA),
  KWT(new Locale("", "KW"), ProviderSearchType.AREA),
  LTU(new Locale("", "LT"), ProviderSearchType.AREA),
  LUX(new Locale("", "KU"), ProviderSearchType.AREA),
  LVA(new Locale("", "LV"), ProviderSearchType.AREA),
  NLD(new Locale("", "NL"), ProviderSearchType.AREA),
  NOR(new Locale("", "NO"), ProviderSearchType.AREA),
  NZL(new Locale("", "NZ"), ProviderSearchType.AREA),
  POL(new Locale("", "PL"), ProviderSearchType.AREA),
  PRT(new Locale("", "PT"), ProviderSearchType.AREA),
  QAT(new Locale("", "QA"), ProviderSearchType.AREA),
  ROU(new Locale("", "RO"), ProviderSearchType.AREA),
  RUS(new Locale("", "RU"), ProviderSearchType.AREA),
  SAU(new Locale("", "SA"), ProviderSearchType.AREA),
  SVK(new Locale("", "SK"), ProviderSearchType.AREA),
  SVN(new Locale("", "SI"), ProviderSearchType.AREA),
  SWE(new Locale("", "SE"), ProviderSearchType.AREA),
  TUR(new Locale("", "TR"), ProviderSearchType.AREA),
  UKR(new Locale("", "UA"), ProviderSearchType.AREA),
  VNM(new Locale("", "VN"), ProviderSearchType.AREA),
  ;

  private final Locale locale;
  private final ProviderSearchType providerSearchType;
  private boolean zipContainsLetters = false;

  EpgCountry(Locale locale, ProviderSearchType providerSearchType) {
    this.locale = locale;
    this.providerSearchType = providerSearchType;
  }

  EpgCountry(Locale locale, ProviderSearchType providerSearchType, boolean zipContainsLetters) {
    this.locale = locale;
    this.providerSearchType = providerSearchType;
    this.zipContainsLetters = zipContainsLetters;
  }

  public String getDisplayName() {
    return locale.getDisplayCountry();
  }

  public ProviderSearchType getProviderSearchType() {
    return providerSearchType;
  }

  public boolean zipContainsLetters() {
    return zipContainsLetters;
  }
}
