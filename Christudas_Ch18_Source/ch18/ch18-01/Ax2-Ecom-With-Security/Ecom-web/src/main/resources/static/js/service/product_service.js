'use strict';

ecomApp.factory('ProductService', ['$http', '$q', function($http, $q){

	return {
		
		     getProductsByCategory: function(category) {
			 return $http.get(sessionStorage.getItem('apiUrl')+'/product/productsByCategory?category='+category)
					.then(
							function(response){
								return response.data._embedded.productdata;
							}, 
							function(errResponse){
								console.error('Error while fetching products by category');
								return $q.reject(errResponse);
							}
					);
	         },
	  
	     getProductCategories: function() {
		 return $http.get(sessionStorage.getItem('apiUrl')+'/product/categories/')
				.then(
						function(response){
							return response.data._embedded.categories;
						}, 
						function(errResponse){
							console.error('Error while fetching products category');
							return $q.reject(errResponse);
						}
				);
         },
         
         getInventoryByProduct: function(productCode) {
			 return $http.get(sessionStorage.getItem('apiUrl')+'/product/inventory/search/findBySku?sku='+productCode)
					.then(
							function(response){
								return response.data;
							}, 
							function(errResponse){
								console.error('Error while fetching inventory of producs');
								return $q.reject(errResponse);
							}
					);
	         },
	      getProductByName: function(name) {
				 return $http.get(sessionStorage.getItem('apiUrl')+'/product/productsByName?name='+name)
						.then(
								function(response){
									return response.data._embedded.productdata;
								}, 
								function(errResponse){
									console.error('Error while fetching producs by name');
									return $q.reject(errResponse);
								}
						);
		         },
         getProductById: function(id) {
					 return $http.get(sessionStorage.getItem('apiUrl')+'/product/products/'+id)
							.then(
									function(response){
										return response.data;
									}, 
									function(errResponse){
										console.error('Error while fetching producs by id');
										return $q.reject(errResponse);
									}
							);
			         }        
	};

}]);
