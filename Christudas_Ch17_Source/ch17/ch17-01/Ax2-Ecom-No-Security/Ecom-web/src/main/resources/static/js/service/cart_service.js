'use strict';
ecomApp.factory('CartService', ['$http', '$q', function($http, $q){

	return {
		
		     getUserCart: function(userId) {
			 return $http.get(sessionStorage.getItem('apiUrl')+'/cart/customerCart/'+userId)
					.then(
							function(response){
								return response.data;
							}, 
							function(errResponse){
								console.error('Error while fetching usercarts');
								return $q.reject(errResponse);
							}
					);
	         },
	  
	     updateUserCart: function(customerCartDTO) {
		  return $http.post(sessionStorage.getItem('apiUrl')+'/cart/customerCart/',customerCartDTO)
				.then(
						function(response){
							return response.data;
						}, 
						function(errResponse){
							console.error('Error while updating customer cart');
							return $q.reject(errResponse);
						}
				);
         }
         
        
	};

}]);
