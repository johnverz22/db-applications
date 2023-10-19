# Adding Search
## 1. (Optional) Add enclosing `div` to the whole table and add `form` to accept user input for search.
```html
<div>
    <div>
        <form>
            <input type="text" id="search-input">
            <button onclick="search(event)">Search</button>
        </form>
    </div>
    <table>

    ...
    </table>
</div>    
```
The `onclick` attribute in the `button` element adds an event listener to it. When it is clicked the function `search()` will be exectured passing the event as an arguemnt.

## 2. Edit the `table` by wrapping the heading with `thead` and adding an empty `tbody`. This way, it will be easier to populate the table with dynamic content.
```html
<table id="data-table">
    <thead>
        <tr>
            <th>No.</th>
            ...
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
```
The `tbody` will contain all the dynamic content fetched through ajax.

## 3. In the javascript, edit the query selector of the `table`.
From:
```javascript
const table = document.querySelector('table');
```
To:
```javascript
const table = document.querySelector('table tbody');
```

## 4. In the javascript, create a function `search()` to handle the search button click event. Add the following javascript code.
```javascript
function search(event) {
	//prevent to leave the page (or reload it)
	event.preventDefault(); 

	//get the search text field value
	const searchTerm = document.getElementById('search-input').value;

	// Get a reference to the table body
	const table = document.querySelector('table tbody');

	// Clear the existing table content
	tableBody.innerHTML = '';

	// Fetch data from the '/api/search' path or route or context with the search term
    fetch(`http://localhost:8000/search?q=${searchTerm}`)
        .then(response => response.json())
        .then(jsonData => {
            // Populate the table with JSON data
            jsonData.forEach((data) => {
                const row = document.createElement('tr');
                // Create and append table cells (td) similar to the fetch function above
                const tdID = document.createElement('td');
                tdID.textContent = data.id;
                //etc.

                //add each cell as child to the tr (row)
                row.appendChild(tdID);
                //etc

                //add each the row to the table body
                table.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching data:', error);
    });
}
```
Do not just copy the code as is. This is just a pattern to guide you. Read and understand it.

## 5. Create additional context or route or path in your server to serve a filtered data based on the search input.
This is the `/api/search` specified in the `fetch()` request.

For Java:
```java
server.createContext("/api/search", exchange -> {
	//this will remove the "q=" and retain only the value 
	//for instance, if you searched "apple", the request will include query parameter - q=apple
    String keyword = exchange.getRequestURI().getQuery().substring(2); 
    
    //create a StringBuilder object for the JSON
    StringBuilder jsonData = new StringBuilder();
    jsonData.append("["); //start an array of JSON objects
    try{
        //Initialize database connection
        Connection dbConnection = DriverManager.getConnection(dbURL,userName, password);
        
        //SQL query to read data with filter
        String sql = "SELECT * FROM persons WHERE first_name LIKE ? OR last_name LIKE ? OR gender LIKE ? OR email LIKE ?";
        
        //Create a PreparedStatement
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        
        //Fill in the SQL parameters ? with their actual value. There are 4 parameters (?)
        preparedStatement.setString(1, "%"+keyword+"%"); //first_name
        preparedStatement.setString(2, "%"+keyword+"%"); //last_name
		preparedStatement.setString(3, keyword+"%"); //gender
		preparedStatement.setString(4, "%"+keyword+"%"); //email

        //Execute the query and get the result set
        ResultSet resultSet = preparedStatement.executeQuery();
        
        //TO DO
        //Process the result why looping it similar to /api/persons
 
        //Append to the JSON String builder

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
For python:
```python
# Route to serve the filtered persons data as JSON
@app.route('/api/search', methods=['GET'])
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
	    sql = query = """
		    SELECT *
		    FROM persons
		    WHERE first_name LIKE %s
		    OR last_name LIKE %s
		    OR gender LIKE %s
		    OR email LIKE %s
		"""

		# Execute the query with a single tuple of parameters
		cursor.execute(query, (search, search, search, search))
	    

	    # Fetch all the data from the executed query
	    data = cursor.fetchall()
	    
	    # Close the database connection
	    connection.close()
	    
	    # Initialize an empty list to store the results
	    persons = []
	    # Iterate through the fetched data and format it as a list of dictionaries
	    
	    # FOLLOW THE PATTERN IN /api/persons


	    return jsonify(persons)
	except Exception as e:
	    # If an error occurs during the execution, print the error message
	    print(f"Error: {e}") 
	    # Create an error dictionary and return it as a JSON response
	    error = {"error": e}
	    return jsonify(error)
```
