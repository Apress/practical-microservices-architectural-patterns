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
		  return $http.post(sessionStorage.getItem('apiUrl')+'/customer/validateCustomer?userId='+userCredentialDTO.userName+'&password='+userCredentialDTO.password)
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
	}
}]);
