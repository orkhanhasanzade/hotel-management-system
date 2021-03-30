$(document).ready( function() 
{	
	// datatable
	var dataTable = $('#mailTable').DataTable({
		dom :'Bfrtip',
		"bInfo" : false,	
		buttons : []
	});	
	$("#mailTable thead").remove();	
	$('#searchFilter').on('keyup', function() {
		dataTable.search(this.value).draw();
	});	
	$(function() 
	{
		var actions = $('#inbox-actions .btn'), rows = $('#mailTable tr');
		$("input[data-select='all']").change(function(){
			   $(this).prop('checked') 
			       ? rows.find('.mail-check').prop('checked',true)
			       : rows.find('.mail-check').prop('checked',false)        
		});	
	});
	
	dataTable.on( 'draw', function () {
	    alert( 'Table redrawn' );
	} );
	
	// Delete chosen mails 
	$('.btnDelete').on('click', function(event)
	{	
		var mails = [];
	    $.each($("input[name='mail']:checked"), function(){            
		     mails.push($(this).val());
		});
	
	    if(mails.length != 0)
		{			
	    		$("#warning-page").hide();						    						    	
				$("#spinner").show();
	    		//prevents redirection to blank page with JSON-Task-Object
				event.preventDefault();
				var href = '/mails/' + mails;
			    var href2 = '/get-mails-status/' + mails;			 		
			 		
			 		// jQuery.when()
			 		$.when(	$.get(href, function(data)
							{
								 $("#mailPart").html(data); 
							})				 						 				
					 	  ).then(function( x ) 
					 			{			
									$.get(href2, function(data)
									{
										 if(data == 0)
										 {		$("#spinner").hide();
											 	$("#success-page").show();
												$(".modal-header").show();
												$(".modal-footer").show(); 
										 }  
									});															 			
					 });							 		
		}
	});	

	 // Mark mail as read 
	 // $(document).on added to work after new html element added to page
	 //  To bind the click event to all existing and future elements, use the jQuery on() method.
	 // Also you can change to delegate() method to use delegation from the body ( $("body").delegate )
	 $(document).on("click", ".btnMarkAsRead" , function() 
	 {
			console.log('btnMarkAsRead');
			var mails = [];
		    $.each($("input[name='mail']:checked"), function(){            
			     mails.push($(this).val());
			});
			
		    if(mails.length != 0)
			{
		    	var href = '/markasread/' + mails;
		    	$.get(href, function(data)
		    	{
		    		$("#mailPart").html(data); 
				});
			}         
     });
	
	// Show Inbox mails 
	$(document).on("click", ".btnShowInboxMails" , function() 
	{	
	    	var href = '/mail/inbox';
	    	$.get(href, function(data)
	    	{
	    		 $("#mailbox-container").html(data); 
			});						
	});	
		
	// Show Sent mails 
	$(document).on("click", ".btnShowSentMails" , function() 
	{	
	    	var href = '/mail/sent';
	    	$.get(href, function(data)
	    	{
	    		 $("#mailbox-container").html(data); 
	    		
			});						
	});	

	// Show Trash mails 
	$(document).on("click", ".btnShowTrashMails" , function() 
	{	
	    	var href = '/mail/trash';
	    	$.get(href, function(data)
	    	{
	    		 $("#mailbox-container").html(data); 
			});						
	});
	
	// Compose mail 
	$(document).on("click", ".btnComposeMail" , function() 
	{	
	    	var href = '/mail/compose';
	    	$.get(href, function(data)
	    	{
	    		 $("#mailbox-container").html(data); 
			});						
	}); 
	
	// View mail 
	$(document).on("click", ".btnViewMail" , function() 
	{	
			//prevents redirection to blank page with JSON-Task-Object
			event.preventDefault();
			
			var href = $(this).attr('href');
	    	$.get(href, function(data)
	    	{
	    		 $("#mailbox-container").html(data); 
			});						
	});
	
	// Compose part
	//$("#mailReportForm").submit( function(event) 
	$(document).on("submit", "#mailReportForm" , function() 		
	{ 
			console.log('iyuyuuy');
 			event.preventDefault(); //prevent default action
				    
 			//get form action url  
			let post_url = document.getElementById('mailReportForm').action													
										
			//Encode form elements for submission
			let form_data = $("#mailReportForm :input").serializeArray();
 					
 			if(form_data[0].value != '')
 			{	
 				 if(form_data[1].value != '')
 	 			 {
 					 if(form_data[2].value != '')
 		 			 {
 						  console.log('problem');        
 						  $("#mailPartCompose").empty();	
					 	  $("#mailPartCompose").append('<style>.loader {border: 16px solid #f3f3f3;border-radius: 50%;border-top: 16px solid #3498db;width: 120px;height: 120px;-webkit-animation: spin 2s linear infinite; /* Safari */animation: spin 2s linear infinite;}/* Safari */@-webkit-keyframes spin {0% { -webkit-transform: rotate(0deg);}100% { -webkit-transform: rotate(360deg); }}@keyframes spin { 0% { transform: rotate(0deg); }100% { transform: rotate(360deg); }}</style><div class="mailbox-header d-flex justify-content-between" style="border-bottom: 1px solid #e8e8e8;"><h5 class="inbox-title">Sending...</h5></div><div class="mailbox-body"><div style="width:20%;display:block;margin-left:auto;margin-right:auto;"><div class="loader"></div></div></div>');							 				
		 				  $.get(post_url, form_data, function(response) 					
						  {		
		 						$("#mailPartCompose").empty();	
								$("#mailPartCompose").append(response);					
						  });	
 		 			  }
 	 			  }
 			}
	});				      

});	

//$(document).on("click", ".btnShowSentMails" , function() 
//		{	
//				dataTable.destroy();
//				//$('#mailTable').empty();			
//					var href = '/mail/sent';
//					$.get(href, function(json)
//					{				
//					     //$("#mailbox-container").html(data);
//					  	 $('#mailTable').DataTable( {	 
//					 		 data: json,
//					 		    columns: [
//					 		    	//{ data: 'fromAddress' },
//					 		        { data: 'fromAddress' },
//					 		        { data: 'subject' },		 		      
//					 		        { data: 'readStatus' },
//					 		        { data: 'sentDate' },
//					 		    ]
//					 		    
//					 	    } );
//				
//						    dataTable.clear();
//						    dataTable.rows.add(json);
//						    dataTable.draw();	  	 					  	 
//					});						
//});	
			