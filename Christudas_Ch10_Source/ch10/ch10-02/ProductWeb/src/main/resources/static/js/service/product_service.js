'use strict';

App.factory('ProductService', ['$http', '$q', function($http, $q){

	return {
		
		     getApplication: function() {
			 return $http.get('http://localhost:8082/productsweb/')
					.then(
							function(response){
								return response.data.product;
							}, 
							function(errResponse){
								console.error('Error while fetching application');
								return $q.reject(errResponse);
							}
					);
	         }		
	};

}]);
