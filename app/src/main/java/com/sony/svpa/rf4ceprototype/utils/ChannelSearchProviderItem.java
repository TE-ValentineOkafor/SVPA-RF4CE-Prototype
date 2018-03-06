package com.sony.svpa.rf4ceprototype.utils;

/**
 * Created by Breeze on 3/27/2017.
 */

public class ChannelSearchProviderItem
{
    private String suggest_text_1;
    private String suggest_text_2;
    private String suggest_intent_action;
    private String suggest_intent_data;

    public ChannelSearchProviderItem()
    {

    }

    public ChannelSearchProviderItem(String text_1, String text_2, String intent_action, String intent_data)
    {
        suggest_text_1 = text_1;
        suggest_text_2 = text_2;
        suggest_intent_action = intent_action;
        suggest_intent_data = intent_data;
    }

    public String getSuggestText1() {
        return suggest_text_1;
    }

    public void setSuggestText1(String suggest_text_1) {
        this.suggest_text_1 = suggest_text_1;
    }

    public String getSuggestText2() {
        return suggest_text_2;
    }

    public void setSuggestText2(String suggest_text_2) {
        this.suggest_text_2 = suggest_text_2;
    }

    public String getSuggestIntentAction() {
        return suggest_intent_action;
    }

    public void setSuggestIntentAction(String suggest_intent_action) {
        this.suggest_intent_action = suggest_intent_action;
    }

    public String getSuggestIntentData() {
        return suggest_intent_data;
    }

    public void setSuggestIntentData(String suggest_intent_data) {
        this.suggest_intent_data = suggest_intent_data;
    }
}
