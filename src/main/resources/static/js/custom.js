$(document).ready(function() {
	
	// Display new modal
	$('.btnNew').click({modalId: '#modalNewOrEdit'}, formCallback);
	
	// Display edit modal
	$('.table .btnEdit').click({modalId: '#modalNewOrEdit'}, formCallback);
	
	// Display delete modal
	$('.table .btnDelete').click({modalId: '#modalDelete'}, formCallback);
	
	function formCallback(event) {
		event.preventDefault();
		
		// just display the modal or pre-fill the form for edits
		$.ajax({
			url: $(this).attr('href'), 
			success: function(result) { 
				$('#modalHolder').html(result);
				$(event.data.modalId).modal("show"); // open the correct modal programmatically
//				$('#modalBtnSave').click(formPostCallback); // TODO Use this only on validation errors
			},
			error: function(err) {
				console.log("formCallback(): error occurred: " + JSON.stringify(err.responseJSON));
			}
		});
	}
	
	// in order to handle validation errors
	function formPostCallback(event) {
		event.preventDefault();
		
		$('#modalBtnClose').click(); // dismiss previous modal
		
		// prevent form from automatically POSTing
		$.ajax({
			type: "POST",
			url: $('#formNewOrEdit').attr('action'),
			success: function(result) { 
//				console.log(result);
				$('#modalHolder').html(result);
				$('#modalNewOrEdit').modal("show"); // open the correct modal programmatically
				$('#modalBtnSave').click(formPostCallback);
			},
			error: function(err) {
				console.log("formPostCallback(): error occurred: " + JSON.stringify(err.responseJSON));
			}
		});
	}
	
	
	
	
	
	// Functions for user
	function openEditModal(id) {
	    $.ajax({
	        url: "EditPerson/"+id,
	        success: function(data) {
	        	console.log(data);
	            $("#modalHolder").html(data);
	        	$("#personEditModal").modal();
	        }
	    });
	    var listDevices  = $('#multiselectDevices').multiselect({
	    	includeSelectAllOption: true,
	    	enableFiltering : true
	   	});
	          
	}
	    
   function openDeleteModal(id){
    $.ajax({
        url: "DeletePerson/"+id,
        success: function(data) {
            console.log(data);
             $("#modalHolder").html(data);
        	 $("#personDeleteModal").modal();
            }
        });
   }
	   
   function deleteConfirm(id){
	   var personId={"personId" : id};
	   $.ajax({
	        url: "DeletePersonConfirm/"+id,
	        type:'POST',
	        success:function(data){
	         $("#personDeleteModal").hide();
	           window.location.href="persons";
	           $("#usersTable").ajax.reload();
	        }
	   });
   }
});