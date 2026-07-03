package com.hoteltracker.api;

import java.io.IOException;
import java.util.List;

public class ApiEntry {
    public static void  main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting API data collection...");

        Datacollector dc = new Datacollector();

        String rawJson = dc.fetchData();

        List<HotelRecord> dailyRecords = dc.parseJson(rawJson);

        //save dc.saveToDatabase(dailyRecords, totalCount);
    }
}
