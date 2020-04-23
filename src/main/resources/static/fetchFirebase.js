module.exports =  { fetchFirebase: function () {

	var user = firebase.auth().currentUser;
	var token = "";
	 user.getIdToken().then(function(accessToken){

		token = accessToken;
	
	console.log(token);

	var params = {  
		classroomId : "8abf9cd249024cec92e618b4f0119778" ,
		title : "test",
		description : "first task" ,
		link : "value",
		date : "20/01/20"


	 };
	 var paramNewClass = {  
		name : "2SBO" ,
		description : "la classe de boulange" ,
		

	 };
	var url = '/task/createTask?'; 
	var urlnewClass = '/user/createClassroom?'

	var queryString = Object.keys(params).map(key => key + '=' + params[key]).join('&');
var queryNewClass = Object.keys(paramNewClass).map(key => key + '=' + paramNewClass[key]).join('&');
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
       // Typical action to be performed when the document is ready:
       console.log(xhr.responseText);
   	 }
		};
		
        // xhr.open("POST", urlnewClass + queryNewClass , true);
		// xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');

        // xhr.setRequestHeader('Authorization', token);
		
     
	
		// xhr.send(); 

		xhr.open("POST", url + queryString , true);
		xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');

        xhr.setRequestHeader('Authorization', token);
		
     
	
		xhr.send(); 
	 }
		 );
	

	

}}