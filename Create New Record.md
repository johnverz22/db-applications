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

## 2. Handle save event via Javascript

## 3. Process submitted data in the server

## 4. Refresh table