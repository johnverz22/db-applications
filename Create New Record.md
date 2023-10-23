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

## 2. Handle save event in Javascript
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

## 4. Refresh table