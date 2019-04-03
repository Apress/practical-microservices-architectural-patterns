'use strict';

ecomApp.controller('OrderController', [
		'$scope',
		'$location',
		'OrderService',
		'CartService',
		'ProductService',
		function($scope, $location, OrderService,CartService,ProductService) {

			var orderCtrl = this;
			var user = JSON.parse(sessionStorage.getItem('loggedUser'));
			$scope.user=user;
			$scope.showSuccessMessage=false;
			$scope.showFailMessage=false;
			$scope.inProgressOrders=[];
			$scope.cancelledOrders=[];
			$scope.shippedOrders=[];
			$scope.completedOrders=[];
			$scope.failedOrders=[];
			$scope.productInfo={};
			$scope.productImgs={};
			$scope.init = function () { 
		    	//clear all existing data on reload 
				$scope.inProgressOrders=[];
				$scope.cancelledOrders=[];
				$scope.shippedOrders=[];
				$scope.completedOrders=[];
				$scope.failedOrders=[];
				
				
		    OrderService.getOrderHistory(user.userId).then(
					function(data) {
						for(var i=0;i<data.orderHistory.length;i++){
							orderCtrl.enrichWithProductInfo(data.orderHistory[i]);
							if(data.orderHistory[i].orderStatus=='PAID'){
								$scope.inProgressOrders.push(data.orderHistory[i]);
							}else if(data.orderHistory[i].orderStatus=='CANCELLED'){
								$scope.cancelledOrders.push(data.orderHistory[i]);
							}else if(data.orderHistory[i].orderStatus=='SHIPPED'){
								$scope.shippedOrders.push(data.orderHistory[i]);
							}else if(data.orderHistory[i].orderStatus=='DELIVERED'){
								$scope.completedOrders.push(data.orderHistory[i]);
							}else if(data.orderHistory[i].orderStatus=='DELIVERY_FAILED'){
								$scope.failedOrders.push(data.orderHistory[i]);
							}
							
						}
						
					},
					function(errResponse) {
						console.error('Error while retriving history '
								+ errResponse);
					});
		    }; 
		    orderCtrl.refresh=function(){
		    	$scope.init();
		    };
		    orderCtrl.cancelOrder =function(orderId){
		    	OrderService.cancelOrder(orderId)
	              .then(
	            		  function(data) {
	            			  console.log('Order cancelled ');
	            			  setTimeout(function(){
		            			  $scope.init();
		            			  }, 1000);
	            		  },
	            		  function(errResponse){
					    	   console.error('Error while updating user cache '+errResponse);
	  					  }
	              );
		    };
			
			orderCtrl.addNewOrder = function() {
				$scope.showSuccessMessage=false;
				$scope.showFailMessage=false;
				var orderDTO = {};
				orderDTO.userId = user.userId;
				orderDTO.lineItems = [];
				for (var i = 0; i < $scope.myCart.lineItems.length; i++) {
					orderDTO.lineItems.push({
						inventoryId : $scope.myCart.lineItems[i].inventoryId,
						price : $scope.myCart.lineItems[i].price,
						quantity : $scope.myCart.lineItems[i].quantity,
						productId : $scope.myCart.lineItems[i].id,
						productName :$scope.myCart.lineItems[i].name
					}

					);
				}

				OrderService.addNewOrder(orderDTO).then(
						function(data) {
							if(data=='SUCCESS'){
								$scope.myCart.lineItems=[];
							    CartService.updateUserCart($scope.myCart)
					              .then(
					            		  function(data) {
					            			  $scope.$emit('cartModified', $scope.myCart);
					            			  $scope.showSuccessMessage=true;
					            			  setTimeout(function(){
					            			  $scope.init();
					            			  }, 1000);
					            		  },
					            		  function(errResponse){
									    	   console.error('Error while updating user cache '+errResponse);
					  					  }
					              );
							}else if(data=='OUT_OF_STOCK') {
								$scope.showFailMessage=true;
							}else{
								alert("Error while creating new order")
							}
								
						},
						function(errResponse) {
							console.error('Error while updating user cache '
									+ errResponse);
						});

			};
			
			orderCtrl.enrichWithProductInfo = function(order) {
				for(var i=0;i<order.lineItems.length;i++){
				 ProductService.getProductById(order.lineItems[i].productId)
		         .then(
					       function(data) { 
					    	  
					    	   $scope.productInfo[data.id]=data.name;
					    	   $scope.productImgs[data.id]=data._links.imgUrl.href;
					       },
						  function(errResponse){			    	   
					    		console.error('Error while fetching product by Id'+errResponse);
							}
			       );
				}
				
			};
			

		} ]);
