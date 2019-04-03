'use strict';

ecomApp.controller('ApplicationController', ['$scope','ApplicationService', function($scope,ApplicationService) {
	
	 var appCtrl=this;
	 $scope.products=[];
     $scope.orders=[];
    
     $scope.init = function () { 
    	 appCtrl.getProducts();
    	 appCtrl.getOrders();
     }
     
     appCtrl.getProducts = function(){ 
    	 ApplicationService.loadProducts()
         .then(
			       function(data) {
			    	 $scope.products= data; 
			       },
					function(errResponse){
					 console.error('Error'+errResponse);
					}
	       );
     };
     
     
     appCtrl.getOrders = function(){ 
    	 ApplicationService.loadOrders()
         .then(
			       function(data) {
			    	 $scope.orders= data; 
			       },
					function(errResponse){
					 console.error('Error '+errResponse);
					}
	       );
     };
     appCtrl.doOrder = function(id,price){ 
    	 var orderDTO = {};
    	 orderDTO.productId=id;
    	 orderDTO.price=price;
    	 orderDTO.number=1;
    	 ApplicationService.doOrder(orderDTO)
         .then(
			       function(data) {
			    	   appCtrl.getProducts();
			    	   appCtrl.getOrders();
			       },
					function(errResponse){
					 console.error('Error '+errResponse);
					}
	       );
     };   
     

}]);
