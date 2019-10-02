var Lab4 = ( function() {

    return {

        convert: function(rates) {
            
            /*
             * This function accepts the data object sent by the server
             * ("rates") as an argument.  It should: get the amount (in USD)
             * entered by the user in the "input" form field, iterate through
             * the currency exchange rates given in the data object, multiply
             * each rateby the given number of U.S. Dollars, and compute the
             * corresponding amount for each currency.  These amounts should be
             * shown in the "output" element of the page, along with the
             * currency codes, separated by colons and formatted to two decimal
             * places.  (See the screenshot given with this assignment.)
             */
            // INSERT YOUR CODE HERE
            var date = rates.date;
            var indRates = rates.rates;
            var base = rates.base;
            var userInput = $("#input").val();
            if(isNaN(userInput)){
                alert("Error! Your input is not valid!")
                return;
            }
            else if(userInput.length == 0){
                alert("The required field cannot be empty");
                return;
            }
            else{
                var userNum = parseFloat(userInput);
            }
            Object.keys(indRates).forEach(function(key) {
                //alert(key + " " +  indRates[key]);
                var mult = parseFloat(indRates[key]);
                $("#output").append(key + "&nbsp;" + (userNum * mult).toFixed(2) +  "</br>");
            });
            $("#output").append("</br>");
            $("#output").append("Based on&nbsp;" + date +"&nbsp;exchange rates");
        },
        
        getConversion: function() {
            /*
             * This method should send an Ajax request to our API to get the
             * latest exchange rates.  Use "latest" as the URL and "json" as the
             * data type, so that the data will be automatically parsed to a
             * JavaScript object.  (In the sample code in the "HTTP Basics"
             * lecture notes, this object is called "response".  Then, invoke
             * the helper function "convert()" in the callback function to 
             * perform the conversion.  (If you are unclear about the purpose of
             * the "that" variable shown here, see Page 6 of the "Functions and
             * Objects" lecture notes.
             */
            var that = this;
            
            $.ajax({
                url: 'latest',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    that.convert(response);                    
                }
            });
        },
        
        init: function() {
            /* Output the current version of jQuery (for diagnostic purposes) */            
            $('#output').html( "jQuery Version: " + $().jquery );
        }

    };

}());