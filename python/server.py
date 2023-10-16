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

    
# Check if the script is being run as the main program
if __name__ == '__main__': 
    app.run(debug=True, port=8000) # start the Flask development web server. 