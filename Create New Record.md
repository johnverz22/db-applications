# Create/New Record
This part discusses on how to insert new record in the database from client side to the server side.

## 1. Adding Form
Create a html form.
```html
<div id="create-form">
	<form>
	    <div>
	          <label for="fname">First name</label>
	          <input type="text" id="fname">
	    </div>
	    <div>
	          <label for="lname">Last name</label>
	          <input type="text" id="lname">
	    </div>
	    <div>
	          <label for="gender">Gender</label>
	          <select id="gender">
	              <option value="Male">Male</option>
	              <option value="Female">Female</option>
	          </select>
	    </div>
	    <div>
	          <label for="email">Email Address</label>
	          <input type="text" id="email">
	          <input type="hidden" id="id">
	    </div>
	    <div>
	        <button id="new-save" type="button" onclick="save(event)">Save</button>
	    </div>
	</form>
</div>
```

## 2. Handle Save Click Event in Javascript
Create a separate function to transmit the forms data into the server
```javascript
function save(event){
    event.preventDefault(); // Prevent page from reloading

    // Get reference to input fields
  	const firstName = document.getElementById('fname');
    const lastName = document.getElementById('lname');
    const gender = document.getElementById('gender');
    const email= document.getElementById('email');

    // Send a POST request to add a new entry
    fetch('/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            first_name: firstName.value,
            last_name: lastName.value,
            email: email.value,
            gender: gender.value
        })
    })
    .then(response => response.json())
    .then(jsonData => {
        // Clear the existing table content
        tableBody.innerHTML = '';
        
        //ADD CODE HERE TO REFRESH TABLE (SIMILAR TO GETTING DATA IN THE OTHER FUNCTION)
        
        //Clear form
        firstName.value = '';
        lastName.value = '';
        gender.value = '';
        email.value = '';    
    })
    .catch(error => {
            console.error('Error adding new entry:', error);
    });
}
```

## 3. Process submitted data in the server
Create another context or route in the Java/Python program to process the form's data.
For Java, it is important that you avoid using similar prefix. For example if you want to add `/api/save` and `/api/insert`,
you can use two separate context: `/save` and `/insert`

**FOR JAVA:**

Create another context. Follow the pattern of first contexts that you created

### A. Check if the Request method is `POST`
```java
if ("POST".equals(exchange.getRequestMethod())) {
	// Process data here

} else {
	// Handle invalid HTTP method (e.g., not POST)
    String response = "Invalid request.";
    exchange.getResponseHeaders().set("Content-Type", "text/plain");
    exchange.sendResponseHeaders(405, response.length()); // 405 Method Not Allowed
    OutputStream os = exchange.getResponseBody();
    os.write(response.getBytes());
    os.close();
}	
```
It is important that you prevent illegal access to the context/route.

### B. To retrieve the data submitted by the Javascript `fetch()`
```java
InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
BufferedReader br = new BufferedReader(isr);
StringBuilder requestBody = new StringBuilder();
String line;
while ((line = br.readLine()) != null) {
    requestBody.append(line);
}
```
Import these packages: `java.io.*;`

### C. Add a try...catch to handle `SQLException`
```java
try{
	//Process data here

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
```
### D. Save the data to the database
Add the provided [JSONObject.jar](java/json-20231013.jar) to the library and import the packages in `org.json.*`

Convert the `requestBody` into `JSONObject`:
```java
JSONObject json = new JSONObject(requestBody.toString());
String firstName = json.getString("first_name");
String lastName = json.getString("last_name");
String email = json.getString("email");
String gender = json.getString("gender");
```

Create a connection to the database

Create a SQL statement
```java
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

```

After saving the data, fetch the updated table data by running another `SELECT` query similar to other context.



**FOR PYTHON:**
### A. Create a route that accepts POST request only
```python
@app.route('/create', methods=['POST'])
```
And subsequently define new function to save new record.

### B. To retrieve the data submitted by the Javascript `fetch()`
```python
# Get the JSON data
person = request.get_json()     
# Create a dictionary with person fields
new_person = {
    'first_name' : person.get('first_name'),
    'last_name' : person.get('last_name'),
    'email' : person.get('email'),
    'gender' : person.get('gender')
}

```

### C. Add a try...except to handle `Exception`
Add a `try` and `except` block to handle Exception. Refer to other route

Add database connection and cursor for executing queries

### D. Save the data to the database
Follow the following format on how insert new record.

```python
# Insert query
insert_query = (
    "INSERT INTO persons "
    "(first_name, last_name, email, gender) "
    "VALUES (%(first_name)s, %(last_name)s, %(email)s, %(gender)s)"
)

# Execute a SQL query to save new entry
cursor.execute(insert_query, new_person)

# Commit the changes to the database
connection.commit()
```

Following the pattern in other route, fetch the updated data and
return a JSON response to be used to update the table

## 4. Refresh table in the Javascript
The context will return another JSON object of the updated person table and use it to repopulate the table.
```javascript
//...
.then(jsonData => {
    // Clear the existing table content
    tableBody.innerHTML = '';
    //Refresh table
    jsonData.forEach(data => {
        const row = document.createElement('tr');
        // Create and append table cells
        const idCell = document.createElement('td');
        idCell.textContent = data.id;

        // Continue
    })  
```