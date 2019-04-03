'use strict';

var ecomAdminApp = angular.module('ecomAdminApp', ['ecomApp']);


ecomAdminApp.controller('AdminController', ['$scope','OrderService','UserService','$http','$window', function($scope,OrderService,UserService,$http,$window) {
	
	 var adminCtrl = this;
	 $scope.shippingOrder=null;
	 $scope.deliveringOrder=null;
	 $scope.isAdminLogin=false;
	 $scope.userCredentialDTO={};	 
	 $scope.validuser=true;
	 $scope.init = function () { 		 
		 $http.get('/configController')
			.then(
					function(response){
						$window.sessionStorage.setItem('apiUrl',response.data.apiGatewayURL);
					}, 
					function(errResponse){
						console.error('Error while fetching API gateway URL'+errResponse);
					}
			);
		
     }; 
     
     adminCtrl.doLogin =function(){				 
		 UserService.validateUser($scope.userCredentialDTO)
        .then(
			       function(data) {
			    	   adminCtrl.loadData();
		          },
					  function(errResponse){
					    console.error('Error while validating user user'+errResponse);
					}
			        
	       );
		 
	 };
     
    adminCtrl.loadData =function () { 
		 OrderService.getOrderByStatus('PAID')
        .then(
			       function(data) {
			    	   $scope.isAdminLogin=true;
			    	   $scope.shippingOrder=data;
			    	   adminCtrl.loadShippedData();
			       },
				  function(errResponse){
			    	    $scope.validuser=false;
			        	$scope.isAdminLogin=false;
			    		console.error('Error while fetching orders'+errResponse);
					}
	       );
		 
		
   
   }; 
   
   adminCtrl.loadShippedData =function () { 
	   OrderService.getOrderByStatus('SHIPPED')
       .then(
			       function(data) {
			    	   $scope.deliveringOrder=data;
			       },
				  function(errResponse){
			    		console.error('Error while fetching order'+errResponse);
					}
	       );
   };
   
   adminCtrl.refresh =function () { 
	   adminCtrl.loadData();
   };	   
	   
    adminCtrl.shipOrder =function(order){
    	OrderService.shipOrder(order.orderId)
          .then(
        		  function(data) {
        			  console.log('Order shipped ');
        			  order.disabled=true;
        		  },
        		  function(errResponse){
			    	   console.error('Error while shipping order '+errResponse);
					  }
          );
    };
    
    adminCtrl.deliverOrder =function(order,isDelivered){
    	OrderService.deliverOrder(order.orderId,isDelivered)
          .then(
        		  function(data) {
        			  console.log('Order delivered ');
        			  order.disabled=true;
        		  },
        		  function(errResponse){
			    	   console.error('Error while delivering order '+errResponse);
					  }
          );
    };

}]);
