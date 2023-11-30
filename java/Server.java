import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import org.json.*;

public class Server {

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

            //Create a context for the / path with a handler. This is the home page
            server.createContext("/content-person", exchange -> {
                // Load the HTML file content
                String htmlFilePath = "html/person.html"; // Provide the correct path
                byte[] htmlContent = Files.readAllBytes(Paths.get(htmlFilePath));

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, htmlContent.length);

                // Write the HTML content to the response
                OutputStream os = exchange.getResponseBody();
                os.write(htmlContent);
                os.close();
            });

            server.createContext("/content-hobby", exchange -> {
                // Load the HTML file content
                String htmlFilePath = "html/hobby.html"; // Provide the correct path
                byte[] htmlContent = Files.readAllBytes(Paths.get(htmlFilePath));

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, htmlContent.length);

                // Write the HTML content to the response
                OutputStream os = exchange.getResponseBody();
                os.write(htmlContent);
                os.close();
            });

            // Create a context for serving the CSS
            server.createContext("/css", exchange -> {
                // Load the HTML file content
                String htmlFilePath = "html/style.css"; // Provide the correct path
                byte[] htmlContent = Files.readAllBytes(Paths.get(htmlFilePath));

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "text/css");
                exchange.sendResponseHeaders(200, htmlContent.length);

                // Write the HTML content to the response
                OutputStream os = exchange.getResponseBody();
                os.write(htmlContent);
                os.close();
            });

            // Create a context for serving the JS
            server.createContext("/js", exchange -> {
                // Load the HTML file content
                String htmlFilePath = "html/app.js"; // Provide the correct path
                byte[] htmlContent = Files.readAllBytes(Paths.get(htmlFilePath));

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "text/javascript");
                exchange.sendResponseHeaders(200, htmlContent.length);

                // Write the HTML content to the response
                OutputStream os = exchange.getResponseBody();
                os.write(htmlContent);
                os.close();
            });

            server.createContext("/js-person", exchange -> {
                // Load the HTML file content
                String htmlFilePath = "html/person.js"; // Provide the correct path
                byte[] htmlContent = Files.readAllBytes(Paths.get(htmlFilePath));

                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", "text/javascript");
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
                    String sql = "SELECT * FROM persons "+
                            "WHERE first_name LIKE ? OR last_name LIKE ? "+
                            "OR gender LIKE ? OR email LIKE ?";

                    //Create a PreparedStatement
                    PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
                    preparedStatement.setString(1, "%"+keyword+"%");
                    preparedStatement.setString(2, "%"+keyword+"%");
                    preparedStatement.setString(3, keyword+"%");
                    preparedStatement.setString(4, "%"+keyword+"%");


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
                    preparedStatement.close();
                    dbConnection.close();

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

            server.createContext("/create", exchange -> {
                if ("POST".equals(exchange.getRequestMethod())) {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        requestBody.append(line);
                    }


                    // Parse the JSON and extract data using JSONObject
                    try {
                        JSONObject json = new JSONObject(requestBody.toString());
                        String firstName = json.getString("first_name");
                        String lastName = json.getString("last_name");
                        String email = json.getString("email");
                        String gender = json.getString("gender");

                        // Establish a database connection and insert the data (similar to the previous examples)
                        Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);

                        //SQL query to insert data
                        String sql = "INSERT INTO persons(first_name, last_name, email, gender) VALUES(?,?,?,?)";

                        //Create a PreparedStatement
                        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
                        preparedStatement.setString(1, firstName);
                        preparedStatement.setString(2, lastName);
                        preparedStatement.setString(3, email);
                        preparedStatement.setString(4, gender);


                        //Execute the INSERT command
                        preparedStatement.executeUpdate();


                        //Fetch and return updated table
                        sql = "SELECT * FROM persons";

                        //Prepare statement
                        preparedStatement = dbConnection.prepareStatement(sql);

                        //Execute query
                        ResultSet resultSet = preparedStatement.executeQuery();

                        //Create a JSON array
                        JSONArray jsonArr = new JSONArray();

                        while(resultSet.next()){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", resultSet.getInt("id"));
                            jsonObject.put("first_name", resultSet.getString("first_name"));
                            jsonObject.put("last_name", resultSet.getString("last_name"));
                            jsonObject.put("gender", resultSet.getString("gender"));
                            jsonObject.put("email", resultSet.getString("email"));
                            jsonArr.put(jsonObject);
                        }
                        //Convert JSON array object to String
                        String jsonString = jsonArr.toString();

                        //Close prepared statement
                        preparedStatement.close();

                        //Close database connection
                        dbConnection.close();

                        // Send a response
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, jsonString.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(jsonString.getBytes());
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Handle errors and send an error response
                        String response = "Error adding a new entry.";
                        exchange.getResponseHeaders().set("Content-Type", "text/plain");
                        exchange.sendResponseHeaders(500, response.length()); // 500 Internal Server Error
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } else {
                    // Handle invalid HTTP method (e.g., not POST)
                    String response = "Not Allowed.";
                    exchange.getResponseHeaders().set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(405, response.length()); // 405 Method Not Allowed
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });

            server.createContext("/delete", exchange -> {
                if ("DELETE".equals(exchange.getRequestMethod())){
                    // Extract the item ID to be deleted from the request, for example, from the URI
                    String id = exchange.getRequestURI().getQuery().substring(3);

                    try{
                        //create connection to database
                        Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);

                        //SQL query to insert data
                        String sql = "DELETE from persons WHERE id = ?";

                        //Create a PreparedStatement
                        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
                        preparedStatement.setInt(1, Integer.parseInt(id));

                        //Execute the INSERT command
                        preparedStatement.executeUpdate();


                        //fetch updated table

                        //Fetch and return updated table
                        sql = "SELECT * FROM persons";

                        //Prepare statement
                        preparedStatement = dbConnection.prepareStatement(sql);

                        //Execute query
                        ResultSet resultSet = preparedStatement.executeQuery();

                        //Create a JSON array
                        JSONArray jsonArr = new JSONArray();

                        while(resultSet.next()){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", resultSet.getInt("id"));
                            jsonObject.put("first_name", resultSet.getString("first_name"));
                            jsonObject.put("last_name", resultSet.getString("last_name"));
                            jsonObject.put("gender", resultSet.getString("gender"));
                            jsonObject.put("email", resultSet.getString("email"));
                            jsonArr.put(jsonObject);
                        }
                        //Convert JSON array object to String
                        String jsonString = jsonArr.toString();

                        //Close prepared statement
                        preparedStatement.close();

                        //Close database connection
                        dbConnection.close();

                        // Send a response
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, jsonString.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(jsonString.getBytes());
                        os.close();

                    }catch(SQLException e){
                        e.printStackTrace();
                    }
                } else {
                    // Handle invalid HTTP method
                    String response = "Invalid HTTP method. Only DELETE requests are accepted.";
                    exchange.sendResponseHeaders(405, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                }
            });

            server.createContext("/update", exchange -> {
                // Extract the item ID to be deleted from the request, for example, from the URI
                String id = exchange.getRequestURI().getQuery().substring(3);

                try{
                    //create connection to database
                    Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);

                    //SQL query to insert data
                    String sql = "SELECT * FROM persons WHERE id = ?";

                    //Create a PreparedStatement
                    PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
                    preparedStatement.setInt(1, Integer.parseInt(id));

                    //Execute query
                    ResultSet resultSet = preparedStatement.executeQuery();

                    JSONObject jsonObject = new JSONObject();

                    while(resultSet.next()){
                        jsonObject.put("id", resultSet.getInt("id"));
                        jsonObject.put("first_name", resultSet.getString("first_name"));
                        jsonObject.put("last_name", resultSet.getString("last_name"));
                        jsonObject.put("gender", resultSet.getString("gender"));
                        jsonObject.put("email", resultSet.getString("email"));
                    }

                    //Convert JSON array object to String
                    String jsonString = jsonObject.toString();

                    //Close prepared statement
                    preparedStatement.close();

                    //Close database connection
                    dbConnection.close();

                    // Send a response
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, jsonString.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonString.getBytes());
                    os.close();
                }catch(SQLException e){
                    throw new RuntimeException("An error occurred", e);
                }

            });

            server.createContext("/update-save", exchange -> {
                if ("PUT".equals(exchange.getRequestMethod())) {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        requestBody.append(line);
                    }


                    // Parse the JSON and extract data using JSONObject
                    try {
                        JSONObject json = new JSONObject(requestBody.toString());
                        String firstName = json.getString("first_name");
                        String lastName = json.getString("last_name");
                        String email = json.getString("email");
                        String gender = json.getString("gender");
                        int id = json.getInt("id");

                        // Establish a database connection and insert the data (similar to the previous examples)
                        Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);

                        //SQL query to insert data
                        String sql = "UPDATE persons SET first_name = ?, last_name = ?, email = ?, gender = ? WHERE id = ?";

                        //Create a PreparedStatement
                        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
                        preparedStatement.setString(1, firstName);
                        preparedStatement.setString(2, lastName);
                        preparedStatement.setString(3, email);
                        preparedStatement.setString(4, gender);
                        preparedStatement.setInt(5, id);



                        //Execute the UPDATE command
                        preparedStatement.executeUpdate();


                        //Fetch and return updated table
                        sql = "SELECT * FROM persons";

                        //Prepare statement
                        preparedStatement = dbConnection.prepareStatement(sql);

                        //Execute query
                        ResultSet resultSet = preparedStatement.executeQuery();

                        //Create a JSON array
                        JSONArray jsonArr = new JSONArray();

                        while(resultSet.next()){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", resultSet.getInt("id"));
                            jsonObject.put("first_name", resultSet.getString("first_name"));
                            jsonObject.put("last_name", resultSet.getString("last_name"));
                            jsonObject.put("gender", resultSet.getString("gender"));
                            jsonObject.put("email", resultSet.getString("email"));
                            jsonArr.put(jsonObject);
                        }
                        //Convert JSON array object to String
                        String jsonString = jsonArr.toString();

                        //Close prepared statement
                        preparedStatement.close();

                        //Close database connection
                        dbConnection.close();

                        // Send a response
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, jsonString.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(jsonString.getBytes());
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Handle errors and send an error response
                        String response = "Error adding a new entry.";
                        exchange.getResponseHeaders().set("Content-Type", "text/plain");
                        exchange.sendResponseHeaders(500, response.length()); // 500 Internal Server Error
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } else {
                    // Handle invalid HTTP method (e.g., not POST)
                    String response = "Not Allowed.";
                    exchange.getResponseHeaders().set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(405, response.length()); // 405 Method Not Allowed
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });

            server.setExecutor(null);
            server.start(); //Start the server
            System.out.println("Server is running at  " + port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

