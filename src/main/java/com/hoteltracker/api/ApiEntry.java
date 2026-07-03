package com.hoteltracker.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class ApiEntry {
    public static void  main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting API data collection...");

        Datacollector dc = new Datacollector();
        Gson gson = new Gson();

        String rawJson = dc.fetchData();

        List<HotelRecord> dailyRecords = dc.parseJson(rawJson);
        BookingResponse wrapper = gson.fromJson(rawJson, BookingResponse.class);
        int totalCount = wrapper.getTotalCountWithFilters();

        dc.saveToDatabase(dailyRecords, totalCount);

        System.out.println("Data collection complete. " + dailyRecords.size() + " rows inserted.");
    }
}
