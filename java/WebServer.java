import com.sun.net.httpserver.HttpServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class WebServer {

    public static void main(String[] args) {
        //SET UP DATABASE CONNECTION
        String dbURL = "jdbc:mariadb://localhost:3307/person_db"; //ip:port/db_name
        String userName = "root";
        String password = "1234";


        //SET UP WEB SERVER FOR CLIENT APPS
        try{
            int port  = 8000; //Port number for the HTTP server

            //Create a HttpServer object for HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            //Create a context for the / path with a handler. This is the home page
            server.createContext("/", exchange -> {
                // Load the HTML file content
                String htmlFilePath = "html/index.html"; // Provide the correct path
                byte[] htmlContent = Files.readAllBytes(Paths.get(htmlFilePath));

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, htmlContent.length);

                // Write the HTML content to the response
                OutputStream os = exchange.getResponseBody();
                os.write(htmlContent);
                os.close();
            });

            //Create a context that sends a JSON data
            server.createContext("/data", exchange -> {
                StringBuilder jsonData = new StringBuilder();
                jsonData.append("["); //start an array of JSON objects

                try{
                    //Initialize database connection
                    Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);

                    //SQL query to read data
                    String sql = "SELECT * FROM persons";

                    //Create a PreparedStatement
                    PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);

                    //Execute the query and get the result set
                    ResultSet resultSet = preparedStatement.executeQuery();

                    //Process the result why looping it
                    while (resultSet.next()){
                        //Retrieve row values
                        int id = resultSet.getInt("id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String email = resultSet.getString("email");
                        String gender = resultSet.getString("gender");

                        /*
                        Continue the JSON array by adding this current row, following format:
                        {
                            "id": id,
                            "first_name": "first_name",
                            "last_name": "last_name",
                            "email": "email",
                            "gender": "gender"
                        }
                        */
                        jsonData.append("{" +
                                "\"id\": " + id +
                                ", \"first_name\": \"" + firstName + "\"" +
                                ", \"last_name\": \"" + lastName + "\"" +
                                ", \"email\": \"" + email + "\"" +
                                ", \"gender\": \"" + gender + "\"" +
                        "}");

                        if (!resultSet.isLast()){
                            jsonData.append(",");
                        }
                    }

                }catch (SQLException sqlE){
                    sqlE.printStackTrace();
                }

                jsonData.append("]"); //close the array of object


                // Set response headers and write the JSON response
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonData.length());
                OutputStream os = exchange.getResponseBody();
                os.write(jsonData.toString().getBytes());
                os.close();
            });

            server.createContext("/search", exchange -> {
                String keyword = exchange.getRequestURI().getQuery().substring(2);

                System.out.println(keyword);

                StringBuilder jsonData = new StringBuilder();
                jsonData.append("["); //start an array of JSON objects

                try{
                    //Initialize database connection
                    Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);

                    //SQL query to read data
                    String sql = "SELECT * FROM persons WHERE first_name LIKE ?";

                    //Create a PreparedStatement
                    PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
                    preparedStatement.setString(1, keyword+"%");

                    //Execute the query and get the result set
                    ResultSet resultSet = preparedStatement.executeQuery();

                    //Process the result why looping it
                    while (resultSet.next()){
                        //Retrieve row values
                        int id = resultSet.getInt("id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String email = resultSet.getString("email");
                        String gender = resultSet.getString("gender");

                        /*
                        Continue the JSON array by adding this current row, following format:
                        {
                            "id": id,
                            "first_name": "first_name",
                            "last_name": "last_name",
                            "email": "email",
                            "gender": "gender"
                        }
                        */
                        jsonData.append("{" +
                                "\"id\": " + id +
                                ", \"first_name\": \"" + firstName + "\"" +
                                ", \"last_name\": \"" + lastName + "\"" +
                                ", \"email\": \"" + email + "\"" +
                                ", \"gender\": \"" + gender + "\"" +
                                "}");

                        if (!resultSet.isLast()){
                            jsonData.append(",");
                        }
                    }

                }catch (SQLException sqlE){
                    sqlE.printStackTrace();
                }

                jsonData.append("]"); //close the array of object


                // Set response headers and write the JSON response
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonData.length());
                OutputStream os = exchange.getResponseBody();
                os.write(jsonData.toString().getBytes());
                os.close();
            });

            server.setExecutor(null);
            server.start(); //Start the server
            System.out.println("Server is running at  " + port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


