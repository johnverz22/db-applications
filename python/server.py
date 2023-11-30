import mariadb
from flask import Flask, jsonify, request, send_file
 
#  Create an instance of the Flask application 
app = Flask(__name__)

# Database connection  parameters
db_params = {
    "host": "localhost",
    "port": 3307,
    "user": "root",
    "password": "1234",
    "database": "person_db",
}

# Route to serve home page
@app.route('/')
def index():
    return send_file('index.html')

# Route to serve the persons data as JSON
@app.route('/data', methods=['GET'])
def get_persons():

    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        # Execute a SQL query to search for persons whose first name contains the search string
        cursor.execute("SELECT * FROM persons")

        # Fetch all the data from the executed query
        data = cursor.fetchall()

        # Close the cursor and database connection
        cursor.close()
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

# Route for search
@app.route('/search', methods=['GET'])
def search_persons():
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

        # Execute a SQL query to search for persons
        query = (
            "SELECT * FROM persons "
            "WHERE first_name LIKE %s "
            "OR last_name LIKE %s "
            "OR gender LIKE %s "
            "OR email LIKE %s"
        )

        cursor.execute(query, (search, search, search, search))


        # Fetch all the data from the executed query
        data = cursor.fetchall()

        # Close the cursor and the database connection
        cursor.close()
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

# Route for saving new person
@app.route('/create', methods=['POST'])
def insert_person():
    # Get the JSON data
    person = request.get_json()
        
    # Create a dictionary with person fields
    new_person = {
        'first_name' : person.get('first_name'),
        'last_name' : person.get('last_name'),
        'email' : person.get('email'),
        'gender' : person.get('gender')
    }
    

    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        
        insert_query = (
            "INSERT INTO persons "
            "(first_name, last_name, email, gender) "
            "VALUES (%(first_name)s, %(last_name)s, %(email)s, %(gender)s)"
        )

        # Execute a SQL query to save new entry
        cursor.execute(insert_query, new_person)

        # Commit the changes to the database
        connection.commit()

        # Fetch updated data
        cursor.execute("SELECT * FROM persons")

        # Fetch all the data from the executed query
        data = cursor.fetchall()


        # Close the cursor and database connection
        cursor.close()
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

# Route for deleting person record
@app.route('/delete', methods=['DELETE'])
def delete_person():
    # Retrieve the "id" parameter from the query string of the request
    id = request.args.get("id")

    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        # Execute the DELETE query
        delete_query = "DELETE FROM persons WHERE id = %s"
        cursor.execute(delete_query, (id,))

        # Commit the changes to the database
        connection.commit()

        # Fetch updated data
        cursor.execute("SELECT * FROM persons")

        # Fetch all the data from the executed query
        data = cursor.fetchall()

        # Close the cursor and database connection
        cursor.close()
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

# Route for fetching single record to be updated
@app.route('/update', methods=['GET'])
def update_person():
    # Retrieve the "id" parameter from the query string of the request
    id = request.args.get("id")

    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        # Execute a SQL query to fetch a single person's record to be updated
        query = "SELECT * FROM persons WHERE id = %s"

        cursor.execute(query, (id,))

        # Fetch the result
        record = cursor.fetchone()

        # Close the cursor and database connection
        cursor.close()
        connection.close()

        if record:
            person = {
                "id": record[0],
                "first_name": record[1],
                "last_name": record[2],
                "gender": record[3],
                "email": record[4],
            }
            return jsonify(person), 200  # Return the record as JSON
        else:
            return jsonify({'message': 'Record not found'}), 404

    except Exception as e:
        # If an error occurs during the execution, print the error message
        print(f"Error: {e}")
        
        # Create an error dictionary and return it as a JSON response
        error = {"error": e}
        return jsonify(error)

# Route for saving new person
@app.route('/update-save', methods=['PUT'])
def update_save():
    # Get the JSON data
    person = request.get_json()

    try:
        # Establish a connection to the MariaDB database using provided connection parameters
        connection = mariadb.connect(**db_params)
        
        # Create a cursor for executing SQL queries
        cursor = connection.cursor()

        
        update_query = (
            "UPDATE persons SET first_name = %s, "
            "last_name = %s, email = %s, gender = %s "
            "WHERE id = %s"
        )
        param = (person['first_name'], person['last_name'], person['email'], person['gender'], person['id'])

        # Execute a SQL query to save new entry
        cursor.execute(update_query, param)

        # Commit the changes to the database
        connection.commit()

        # Fetch updated data
        cursor.execute("SELECT * FROM persons")

        # Fetch all the data from the executed query
        data = cursor.fetchall()

        # Close the cursor and database connection
        cursor.close()
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


# Check if the script is being run as the main program
if __name__ == '__main__': 
    app.run(debug=True, port=8000, host='localhost') # start the Flask development web server.