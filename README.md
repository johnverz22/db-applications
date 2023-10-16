# Database and Application Architecture
![Local Image](docs/images/web-architecture)

# Java Server and MariaDB
In this guide, we will create a Java JSON server using  MariaDB. 
This server will serve JSON data.

## Prerequisites

Before we start, make sure you have the following prerequisites installed on your system:

- [MariaDB](https://mariadb.org/) (for the database)
- [MariaDB Connector/J](https://mariadb.com/download-confirmation?group-name=Data%20Access&release-notes-uri=https%3A%2F%2Fmariadb.com%2Fkb%2Fen%2Flibrary%2Fmariadb-connector-j-release-notes%2F&documentation-uri=https%3A%2F%2Fmariadb.com%2Fkb%2Fen%2Flibrary%2Fmariadb-connector-j%2F&download-uri=https%3A%2F%2Fdlm.mariadb.com%2F3418100%2FConnectors%2Fjava%2Fconnector-java-3.2.0%2Fmariadb-java-client-3.2.0.jar&product-name=Java%208%2B%20connector&download-size=635.47%20KB/) (to connect Java and MariaDB)

After downloading the JAR file, create a Java project (suggestion: Use IntelliJ) and import the JAR file to the project

## Code Structure
1. Setup Database connection
```Java
String dbURL = "jdbc:mariadb://localhost:3306/database_name"; 
String userName = "user";
String password = "password";
```

2. Create a HTTP server
```Java
int port  = 80; 

HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
```
3. Create HTTP serve context or routes which can be accessed by the client. Use the database connection to fetch data:
```Java
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
```
See this [file](java/WebServer.java) for complete code.

# Python Server with Flask and MariaDB

In this guide, we will create a Python JSON server using Flask and MariaDB. 
This server will serve JSON data.

## Prerequisites

Before we start, make sure you have the following prerequisites installed on your system:

- [MariaDB](https://mariadb.org/) (for the database)
- [Flask](https://flask.palletsprojects.com/) (for the web server)

You can install the required modules using the following commands:

```bash
pip install mariadb flask
```

## Code Structure
1. Import necessary modules for your code to work correctly such `mariadb` and `flask`


```python
import mariadb
from flask import Flask, jsonify, request
```

2. Define database connection  parameters
```python
db_params = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "password",
    "database": "database_name",
}
```

3. Create routes. Routes are URL pattern associated with a specific action or resource on a web server. For instance the URL `http://localhost/index`, `/index` is the route.

These code provides two distinct routes, the one serving home page and the other serving a JSON data of the persons table

```python
# Route to serve home page
@app.route('/')
def index():
    return send_file('index.html')

# Route to serve the persons data as JSON
@app.route('/api/persons', methods=['GET'])
def get_persons():
    # Retrieve the "q" parameter from the query string of the request
    search = request.args.get("q")

    # Check if search param is present, then create SQL LIKE pattern
    if search: 
        search = f"%{search}%"
    else:
        search = "%"


    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        # Execute a SQL query to search for persons whose first name contains the search string
        cursor.execute("SELECT * FROM persons WHERE first_name LIKE %s", (search,))

        # Fetch all the data from the executed query
        data = cursor.fetchall()

        # Close the database connection
        connection.close()

        # Initialize an empty list to store the results
        persons = []

        # Iterate through the fetched data and format it as a list of dictionaries
        for row in data:
            person = {
                "id": row[0],
                "first_name": row[1],
                "last_name": row[2],
                "gender": row[3],
                "email": row[4],
            }
            persons.append(person)

        # Return the results as a JSON response
        return jsonify(persons)

    except Exception as e:
        # If an error occurs during the execution, print the error message
        print(f"Error: {e}")
        
        # Create an error dictionary and return it as a JSON response
        error = {"error": e}
        return jsonify(error)
```

See this [file](python/server.py) for complete code.

4. Run the script
```bash
python3 server.py
```
or

```bash
py server.py
```
