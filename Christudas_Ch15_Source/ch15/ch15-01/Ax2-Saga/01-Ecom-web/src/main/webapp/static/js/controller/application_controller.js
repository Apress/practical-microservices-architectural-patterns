'use strict';

ecomApp.controller('ApplicationController', ['$scope','ApplicationService', function($scope,ApplicationService) {
	
	 var appCtrl=this;
	 $scope.products=[];
     $scope.orders=[];
     $scope.orderAudits=[];
    
     $scope.init = function () { 
    	 appCtrl.reloadData();
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
     
     appCtrl.getOrderAudits = function(){ 
    	 ApplicationService.loadAudit()
         .then(
			       function(data) {
			    	 $scope.orderAudits= data; 
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
			    	   appCtrl.reloadData();
			       },
					function(errResponse){
					 console.error('Error '+errResponse);
					}
	       );
     };   
     
     appCtrl.doCancel = function(id){ 
    	 ApplicationService.updateOrder(id,'CANCELLED')
         .then(
			       function(data) {
			    	   appCtrl.reloadData();
			       },
					function(errResponse){
					 console.error('Error '+errResponse);
					}
	       );
     };   
     
     appCtrl.doConfirm = function(id){ 
    	 ApplicationService.updateOrder(id,'CONFIRMED')
         .then(
			       function(data) {
			    	   appCtrl.reloadData();
			       },
					function(errResponse){
					 console.error('Error '+errResponse);
					}
	       );
     };   
     
     appCtrl.reloadData = function(){
      appCtrl.getProducts();
  	   appCtrl.getOrders();
  	   appCtrl.getOrderAudits();
     };

}]);
