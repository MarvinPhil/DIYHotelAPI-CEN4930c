package com.hoteltracker.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
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
                    .uri(URI.create("https://booking-com.p.rapidapi.com/v1/hotels/search?page_number=0&dest_type=city&dest_id=20088325&units=metric&children_number=2&locale=en-us&categories_filter_ids=class%3A%3A2%2Cclass%3A%3A4%2Cfree_cancellation%3A%3A1&children_ages=5%2C0&include_adjacency=true&filter_by_currency=USD&order_by=popularity&checkin_date=2026-08-28&checkout_date=2026-08-29&room_number=1&adults_number=2"))//<-/**/
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

    public void saveToDatabase(List<HotelRecord> dailyRecords, int total, BookingResponse wrapper){
        System.out.println("Saving Hotel Records to database...");

        //String captureDate = java.time.LocalDate.now().toString();

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String captureDate = now.format(formatter);

        String targetStayDate = "2026-08-28";

        String hotelsql = "INSERT INTO hotelprices(snapshot_id, hotel_name, price, currency) VALUES (?, ?, ?, ?)";
        String dailysql = "INSERT INTO dailysnapshots(capture_date, target_stay_date, total_hotels_available) VALUES (?, ?, ?)";


        Connection conn = null;




        try {
            conn = DriverManager.getConnection(URLTwo);
            System.out.println("Connected to SQLite database.");

            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            }

            PreparedStatement hotelStmt = conn.prepareStatement(hotelsql);
            PreparedStatement dailyStmt = conn.prepareStatement(dailysql, Statement.RETURN_GENERATED_KEYS);



            int generatedSnapshotId = -1;

            //snapshot table
            try (dailyStmt) {
                dailyStmt.setString(1, captureDate);
                dailyStmt.setString(2, targetStayDate);
                dailyStmt.setInt(3, total);
                dailyStmt.executeUpdate();

                // Read the generated snapshot_id out of the database memory

                try (ResultSet rs = dailyStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedSnapshotId = rs.getInt(1);
                    }
                }}

            try(hotelStmt){
                for (HotelRecord record : dailyRecords){
                hotelStmt.setInt(1, generatedSnapshotId);
                hotelStmt.setString(2, record.getHotel_name());
                hotelStmt.setDouble(3, record.getMin_total_price());
                hotelStmt.setString(4, record.getCurrencycode());

                hotelStmt.addBatch();

            }

                //Send all rows to the database in a single network trip
                int[] rowsInserted = hotelStmt.executeBatch();
                System.out.println("Data added successfully. Rows inserted: " + rowsInserted.length);
            }




        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }


    }



}
