package com.example.intigritirobotics.ui.offers;



public class OfferViewModel {

    private String Banner;
    private String OfferID;
    private boolean Expired;

    public OfferViewModel(String banner, String offerID, boolean expired) {
        Banner = banner;
        OfferID = offerID;
        Expired = expired;
    }

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    public String getOfferID() {
        return OfferID;
    }

    public void setOfferID(String offerID) {
        OfferID = offerID;
    }

    public boolean isExpired() {
        return Expired;
    }

    public void setExpired(boolean expired) {
        Expired = expired;
    }
}