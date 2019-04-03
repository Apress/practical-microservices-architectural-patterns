'use strict';

App.factory('ProductService', ['$http', '$q', function($http, $q){

	return {
		
		     getApplication: function() {
			 return $http.get('http://localhost:8081/productsweb/')
					.then(
							function(response){
								return response.data._embedded.products;
							}, 
							function(errResponse){
								console.error('Error while fetching application');
								return $q.reject(errResponse);
							}
					);
	         }		
	};

}]);
