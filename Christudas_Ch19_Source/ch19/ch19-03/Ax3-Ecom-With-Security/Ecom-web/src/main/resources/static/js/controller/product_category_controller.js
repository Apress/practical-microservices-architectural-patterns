'use strict';
ecomApp.controller('ProductCategoryController', ['$scope', 'ProductService','$location', function($scope, ProductService,$location) {
         
	 var prodCategoryCtrl=this;	   
	 
     prodCategoryCtrl.loadProducts = function(code){
    	 $location.url('/productSearch?category='+code);
     }
          
  }]);
