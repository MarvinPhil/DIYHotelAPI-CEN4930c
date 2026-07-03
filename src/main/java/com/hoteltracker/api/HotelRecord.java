package com.hoteltracker.api;

import java.time.LocalDate;

public class HotelRecord {
    private String captureDate = LocalDate.now().toString();
    private String hotel_name;
    private String targetStayDate = "2026-08-28";
    private double min_total_price;
    private String currencycode;
    private int total_count_with_filters;

    public void setTotalCountWithFilters(int count) {
        this.total_count_with_filters = count;
    }



    public String toString(){
        return "\nHotel Entry: "+ hotel_name +" | "+targetStayDate+" | "+ min_total_price +
                " "+ currencycode +" | "+
                " \n "+ captureDate;
    }


}
