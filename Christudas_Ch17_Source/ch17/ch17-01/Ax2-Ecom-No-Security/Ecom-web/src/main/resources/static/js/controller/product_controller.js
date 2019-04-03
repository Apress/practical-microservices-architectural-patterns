'use strict';
ecomApp.controller('ProductController', ['$scope', 'ProductService','CartService','$location','$routeParams', function($scope, ProductService,CartService,$location,$routeParams) {
         
	var prodCtrl=this;
	 $scope.inventory=null;
     $scope.quantities = [];
     $scope.isStockAvailable = true;
     //$scope.quantity=0;
     $scope.showcartMessage=false;
     
     
    $scope.init = function () { 
    	 ProductService.getProductById($routeParams.id)
         .then(
			       function(data) {
			    	   $scope.product=data;
			    	   prodCtrl.getInventoryDetails(data.code);
			       },
				  function(errResponse){			    	   
			    		console.error('Error while fetching product by Id'+errResponse);
					}
	       );
    
    }; 
    
    prodCtrl.getInventoryDetails =function(code){
    	 ProductService.getInventoryByProduct(code)
         .then(
			       function(data) {
			    	   var newOptions=[];
			    	   $scope.inventory=data;
			    	   if(data.quantity>0){
			    	   for (var i = 1; i <= data.quantity; i++) {
			        	    newOptions.push(i);
			        	  }
			    	   $scope.quantities = newOptions;
			    	   $scope.isStockAvailable = true;
			    	   $scope.quantity=$scope.quantities[1];
			    	   }else{
			    		   $scope.isStockAvailable = false;  
			    	   }
			       },
				  function(errResponse){			    	   
			    	$scope.quantities =[]   
			        $scope.inventory=null;
			    	$scope.isStockAvailable = false;  
			    	console.error('Error while fetching inventory of a product'+errResponse);
					}
	       );
         };
    
          
        prodCtrl.addToCart = function () {
        	  CartService.getUserCart(sessionStorage.getItem('sessionUser'))
              .then(
				       function(data) {
				    	   $scope.myCart=data;
				    	   if($scope.myCart.lineItems==null){
				    		   $scope.myCart.lineItems= [];
				    	   }
				    	   $scope.myCart.lineItems.push( {id: $scope.product.id,name : $scope.product.name, price: $scope.product.price, quantity: $scope.quantity ,inventoryId : $scope.inventory.inventoryId});
				    	   CartService.updateUserCart($scope.myCart)
				              .then(
				            		  function(data) {
				            			  $scope.$emit('cartModified', $scope.myCart);
				            			  $scope.showcartMessage=true;
				            		  },
				            		  function(errResponse){
								    	   console.error('Error while updating user cache '+errResponse);
				  					  }
				              );
				           },
  					         function(errResponse){
				    	     console.error('Error while loading user cache'+errResponse);
  					      }
  					         
				       
		           );
        	  
          };
          
        }]);
