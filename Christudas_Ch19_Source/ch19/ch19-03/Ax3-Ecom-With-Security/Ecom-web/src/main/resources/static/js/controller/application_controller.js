'use strict';

ecomApp.controller('ApplicationController', ['$scope','ProductService' ,'$location','CartService','$rootScope', function($scope,ProductService,$location,CartService,$rootScope) {
	
	 var appCtrl=this;
	 $scope.products=[];
     $scope.categories=[];
     $scope.categoryName='';
     $scope.productCount=0;
     $scope.product=null;
     $scope.isCategoryLoaded =false;    
     $scope.searchText=null; 
     $scope.myCart=null;
     $scope.myCartSize=0;
     $scope.user={};
     $scope.screenName='';
     $scope.init = function () { 
    	 if(sessionStorage.getItem('sessionUser')==null){
    	     sessionStorage.setItem('sessionUser',appCtrl.getUniqueId());
    	 }
    	 if(sessionStorage.getItem('loggedUser')!=null){
    	    var user=JSON.parse(sessionStorage.getItem('loggedUser'));
    	    $scope.screenName=user.firstName+ '  '+user.lastName;
    	 }
     }
     
     
     $rootScope.$on('apiUrlRecieved', function (evt, data) { 
    	 appCtrl.loadCart();
     });
    
     appCtrl.getUniqueId = function() {
    	    function _p8(s) {
    	        var p = (Math.random().toString(16)+"000000000").substr(2,8);
    	        return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
    	    }
    	    return _p8() + _p8(true) + _p8(true) + _p8();
    	};
     
     $scope.getProductCatagories = function(){ 
   	  ProductService.getProductCategories()
         .then(
			       function(data) {
			    	 $scope.categories= data; 
			    	 $scope.categoryName= $scope.categories[0].name;
			    	 $scope.$broadcast('category-Loaded', 'categoryLoaded');
			       },
					function(errResponse){
			    	 $scope.categories=[];
					 console.error('Error in getProductCatagories'+errResponse);
					}
	       );
     };
     
     appCtrl.loadCart = function(){  
   	  CartService.getUserCart(sessionStorage.getItem('sessionUser'))
         .then(
			       function(data) {
			    	   $scope.myCart=data;
			    	   if($scope.myCart.lineItems!=null){
			    	     $scope.myCartSize=$scope.myCart.lineItems.length;
			    	   }
			    	   },
					   function(errResponse){
			    	     console.error('Error while loading user cache'+errResponse);
					   }			         
			       
	           );
   	    	 
     };
     
     $scope.$on('cartModified', function (evt, data) {            	  
    	 $scope.myCart=data;
    	 if($scope.myCart.lineItems!=null){
  	       $scope.myCartSize=$scope.myCart.lineItems.length;
    	 }else{
    		 $scope.myCartSize=0;
    	 }  
     });
     
     $scope.$on('userModified', function (evt, data) {            	  
    	 $scope.screenName=data;
     });
     
   appCtrl.searchProducts = function(){ 
	    $location.url('/productSearch?searchText='+$scope.searchText);
    };
     
    $scope.showProductDetails = function (id) {
    	  $location.url('/product/'+id);
  	  
    };
    
    appCtrl.doOrder = function(){ 
	    $location.url('/productSearch?searchText='+$scope.searchText);
    };
    
    $scope.doOrder = function() {
	    if(sessionStorage.getItem('loggedUser')==null){
	    	$location.url('/user');
	    }else{
	    	$location.url('/order');
	    }
 };

}]);
