$(document).ready(function() {

    $(".btnToggle").click(function() {
        $.ajax({
            type: "POST",
            url: $(this).attr('href'), 
            success: function(result) {
                // TODO this is bad practice since AJAX is used!
                window.location.reload();
            },
			error: function(err) {
				console.log("could not update device status: " + JSON.stringify(err.responseJSON));
            }
		});
    });
    
});