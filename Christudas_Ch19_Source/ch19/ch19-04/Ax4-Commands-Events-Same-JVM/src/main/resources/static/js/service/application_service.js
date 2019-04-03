'use strict';
ecomApp.factory('ApplicationService', ['$http', '$q', function($http, $q,$cookies){

	return {
    
         loadProducts: function() {
   		  return $http.get('http://localhost:8080/products')
   				.then(
   						function(response){
   							return response.data;
   						}, 
   						function(errResponse){
   							console.error('errResponseError retriving products'+errResponse);
   							return $q.reject(errResponse);
   						}
   				);
            },
            
            loadOrders: function() {
         		  return $http.get('http://localhost:8080/orders')
         				.then(
         						function(response){
         							return response.data;
         						}, 
         						function(errResponse){
         							console.error('errResponseError retriving orders'+errResponse);
         							return $q.reject(errResponse);
         						}
         				);
                },
                
                doOrder: function(orderDTO) {
           		  return $http.post('http://localhost:8080/orders',orderDTO)
           				.then(
           						function(response){
           							return response.data;
           						}, 
           						function(errResponse){
           							console.error('errResponseError retriving orders'+errResponse);
           							return $q.reject(errResponse);
           						}
           				);
                  },

         
        
	};

}]);
