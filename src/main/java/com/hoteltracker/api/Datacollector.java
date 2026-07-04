package com.hoteltracker.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Datacollector {
    private String apiKey = "24b4f6a8e1msha07236eb39131e8p1ebadbjsn23cdfb3f73d5";
    private static String URL = "jdbc:sqlite:hotel_tracker.db";
    private static String URLTwo = "jdbc:sqlite:C:/letos(previously_sqlitestudio)/letos-4.0.0-windows-x64/Letos/hotel_tracker.db";


    public String fetchData() throws IOException, InterruptedException {
        String JsonR = null;
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://booking-com.p.rapidapi.com/v1/hotels/search?page_number=0&dest_type=city&dest_id=20088325&units=metric&children_number=2&locale=en-us&categories_filter_ids=class%3A%3A2%2Cclass%3A%3A4%2Cfree_cancellation%3A%3A1&children_ages=5%2C0&include_adjacency=true&filter_by_currency=USD&order_by=price&checkin_date=2026-08-28&checkout_date=2026-08-29&room_number=1&adults_number=2"))//<-/**/
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "booking-com.p.rapidapi.com")
                    .GET()
                    .build();

            HttpResponse<String> response = null;

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String jsonResponse = response.body();
            JsonR = jsonResponse;

            //System.out.println("JSON Response: " + jsonResponse);


        } catch(IOException e){
            System.err.println("IOException e: "+e.getMessage());
            throw new RuntimeException(e);

        } catch(InterruptedException f){
            System.err.println("InterruptedException f: "+f.getMessage());
            throw new RuntimeException(f);

        } catch(IllegalArgumentException g) {
            System.out.println("IllegalArgumentException g: "+g.getMessage());
        } catch(SecurityException h) {
            System.out.println("SecurityException h: "+h.getMessage());
        }

        return JsonR;
    }

    public List<HotelRecord> parseJson(String rawJson){
        Gson gson = new Gson();
        //HotelRecord hotelRecord = gson.fromJson(rawJson, HotelRecord.class);
        BookingResponse responseWrapper = gson.fromJson(rawJson, BookingResponse.class);

        System.out.println("Total Hotels Matching Filters: " + responseWrapper.getTotalCountWithFilters());

        List<HotelRecord> hotelRecords = responseWrapper.getResult();

        if (hotelRecords == null) {
            hotelRecords = new ArrayList<>(); // Prevent NullPointerException if empty
        }
        //hotelRecords.add(hotelRecord);
        System.out.println(hotelRecords);
        return hotelRecords;

    }

    public void saveToDatabase(List<HotelRecord> dailyRecords, int total){
        System.out.println("Saving Hotel Records to database...");

        String sql = "INSERT INTO hotel_prices(capture_date, hotel_name, target_stay_date, price, currency, total_matching_filters) VALUES (?, ?, ?, ?, ?, ?)";


        Connection conn = null;


        try {
            conn = DriverManager.getConnection(URLTwo);
            System.out.println("Connected to SQLite database.");
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (HotelRecord record : dailyRecords){
                pstmt.setString(1, record.getCaptureDate());
                pstmt.setString(2, record.getHotel_name());
                pstmt.setString(3, record.gettargetStayDate());
                pstmt.setDouble(4, record.getMin_total_price());
                pstmt.setString(5, record.getCurrencycode());
                pstmt.setInt(6, total);

                pstmt.addBatch();

            }

            // 5. Send all rows to the database in a single network trip
            int[] rowsInserted = pstmt.executeBatch();

            System.out.println("Data added successfully. Rows inserted: " + rowsInserted.length);

        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }


    }



}
