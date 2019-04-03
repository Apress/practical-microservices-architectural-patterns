'use strict';
ecomApp.factory('UserService', ['$http','$httpParamSerializer' ,'$q','$cookies', function($http, $httpParamSerializer,$q,$cookies){

	return {
		
		   
	     addNewuser: function(customerDTO){
		  return $http.post(sessionStorage.getItem('apiUrl')+'/customer/customer/',customerDTO)
				.then(
						function(response){
							return response.data;
						}, 
						function(errResponse){
							console.error('Error while creating new user');
							return $q.reject(errResponse);
						}
				);
         },
         
         
         
	  validateUser: function(userCredentialDTO){ 
		  
		  var data = {
				    grant_type:"password", 
				    username: userCredentialDTO.userName, 
			        password: userCredentialDTO.password, 
			        client_id: "ecom_app"
			    };
		  
		 var encodedData = $httpParamSerializer(data);
		  var authUrl=sessionStorage.getItem('apiUrl')+"/security/oauth/token"
		  var req = {
		            method: 'POST', 
		            url: authUrl,
		            headers: {
		                "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
		            },
		            data: encodedData
		        };

		   return $http(req)
			.then(
					function(response){
					     var token=(response.headers()['x-token']);
						 sessionStorage.setItem("xtoken", token);
						  return $http.get(sessionStorage.getItem('apiUrl')+'/customer/customer')
							.then(
									function(response){
										return response.data;
									}, 
									function(errResponse){
										console.error('Error while getting user info '+errResponse);
										return $q.reject(errResponse);
									}
							);
					}, 
					function(errResponse){
						console.error('Error while validating user'+errResponse);
						return $q.reject(errResponse);
					}
			);  
		  
		  

       }
        
	};

}]);
