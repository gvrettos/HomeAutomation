$(document).ready(function() {
	
	// Display new modal
	$('.btnNew').click({modalId: '#modalNewOrEdit', ajaxType: 'POST'}, formCallback);
	
	// Display edit modal
	$('.table .btnEdit').click({modalId: '#modalNewOrEdit', ajaxType: 'PUT'}, formCallback);
	
	// Display delete modal
	$('.table .btnDelete').click({modalId: '#modalDelete', ajaxType: 'DELETE'}, formCallback);
	
	function formCallback(event) {
		event.preventDefault();

		// just display the modal or pre-fill the form for edits
		$.ajax({
			url: $(this).attr('href'),
			type: event.data.ajaxType,
			success: function(result) { 
				$('#modalHolder').html(result);
				$(event.data.modalId).modal("show"); // open the correct modal programmatically
			},
			error: function(err) {
				console.log("formCallback(): error occurred: " + JSON.stringify(err.responseJSON));
				alert("Something went wrong!\nCheck logs for more information.");
			}
		});
	}
	
});