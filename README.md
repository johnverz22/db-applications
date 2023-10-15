# Database and Application Architecture
![Local Image](docs/images/web-architecture)

# Java Server and MariaDB

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
    "password": "1234",
    "database": "person_db",
}
```

3. Create routes. Routes are URL pattern associated with a specific action or resource on a web server.
For instance the URL `http://localhost/index`, `/index` is the route.

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

    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        # Execute a SQL query to search for persons whose first name contains the search string
        cursor.execute("SELECT * FROM persons WHERE first_name LIKE ?", (f"%{search}%"))

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

See this [file](backend/server.py) for complete code.

4. Run the script
```bash
python3 server.py
```
or

```bash
py server.py
```
