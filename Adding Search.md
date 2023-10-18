# Adding Search
1. (Optional) Add enclosing `div` to the whole table and add `form` to accept user input for search.
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

2. Edit the `table` by wrapping the heading with `thead` and adding an empty `tbody`. This way, it will be easier to populate the table with dynamic content.
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

3.