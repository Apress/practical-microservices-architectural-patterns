'use strict';

ecomApp.controller('CartController', ['$scope','$location','CartService','ProductService', function($scope,$location,CartService,ProductService) {
	
	 var cartCtrl=this;	 
	 $scope.productImgs={}
	 $scope.init = function () { 
		 if($scope.myCart.lineItems!=null){
			for(var i=0;i<$scope.myCart.lineItems.length;i++){
				cartCtrl.enrichWithProductInfo($scope.myCart.lineItems[i].id);
			}
		 }
			
	 }
	 cartCtrl.remove =function(lineItem){
		 var i = $scope.myCart.lineItems.indexOf(lineItem);
		 if(i != -1) {
			 $scope.myCart.lineItems.splice(i, 1);
			 CartService.updateUserCart($scope.myCart)
             .then(
           		  function(data) {
           			  $scope.$emit('cartModified', $scope.myCart);
           		  },
           		  function(errResponse){
				    	   console.error('Error while updating user cache '+errResponse);
 				  }
             );
		 	
		 }
	 };
	 
	 cartCtrl.enrichWithProductInfo = function(productId) {
			 ProductService.getProductById(productId)
	         .then(
				       function(data) { 
				    	   $scope.productImgs[productId]=data._links.imgUrl.href;
				       },
					  function(errResponse){			    	   
				    		console.error('Error while fetching product by Id'+errResponse);
						}
		       );
			
		};
     

}]);
