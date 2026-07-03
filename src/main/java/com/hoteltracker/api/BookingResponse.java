package com.hoteltracker.api;

import java.util.List;

//Wrapper class
public class BookingResponse {
    private List<HotelRecord> result;
    private int total_count_with_filters;

    public List<HotelRecord> getResult() {
        return result;
    }

    public int getTotalCountWithFilters() {
        return total_count_with_filters;
    }
}
