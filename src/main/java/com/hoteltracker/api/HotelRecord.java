package com.hoteltracker.api;

import java.time.LocalDate;

public class HotelRecord {
    //private String captureDate = LocalDate.now().toString();
    private String hotel_name;
    //private String targetStayDate = "2026-08-28";
    private double min_total_price;
    private String currencycode;
   // private int total_count_with_filters;

   /* public int getTotalCountWithFilters() {
        return total_count_with_filters;
    }*/
    /*public String getCaptureDate() {
        return captureDate;
    }*/
    /*public String gettargetStayDate() {
        return targetStayDate;
    }*/
    public String getHotel_name() {
        return hotel_name;
    }
    public double getMin_total_price() {
        return min_total_price;
    }
    public String getCurrencycode() {
        return currencycode;
    }




    public String toString(){
        return "\nHotel Entry: "+ hotel_name +" | "+" | "+ min_total_price +
                " "+ currencycode +" | "+
                " \n ";
    }


}
