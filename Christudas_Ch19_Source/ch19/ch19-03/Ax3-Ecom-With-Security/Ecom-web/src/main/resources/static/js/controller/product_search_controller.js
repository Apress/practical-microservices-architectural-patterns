'use strict';

ecomApp.controller('ProductSearchController', ['$scope', 'ProductService','CartService','$location','$routeParams', function($scope, ProductService,CartService,$location,$routeParams) {
         
              var prodSearchCtrl=this;
                          
              $scope.init = function () { 
             	 if($routeParams.category!=null){
             		 //search by category
             		prodSearchCtrl.getProductsByCategory($routeParams.category);
             	 }
             	 else if($routeParams.searchText!=null){
             		 //search by text
             		prodSearchCtrl.searchProductsByName($routeParams.searchText);
             	 }else{
             		$scope.getProductCatagories();
             	 }
              
              }; 
              
              $scope.$on('category-Loaded', function (evt, state) {            	  
            	  prodSearchCtrl.getProductsByCategory($scope.categoryName);
              });
            
              prodSearchCtrl.getProductsByCategory = function(category){    
                 $scope.categoryName=category;
            	  $scope.product=null;            	
            	  ProductService.getProductsByCategory(category)
                  .then(
         			       function(data) {
         			    	 $scope.products= data; 			    	
         			    	 $scope.productCount=Object.keys(data).length;
         			       },
         					  function(errResponse){
         			    	 $scope.products=[];
         			    	 $scope.productCount=0;
         					 console.error('Error while fetching product from categories'+errResponse);
         					}
         	       );
              };
              
              prodSearchCtrl.searchProductsByName = function(searchText){ 
              	  $scope.categoryName='';
               	  $scope.product=null;
               	  ProductService.getProductByName(searchText)
                     .then(
            			       function(data) {
            			    	 $scope.products= data; 
            			    	 $scope.productCount=Object.keys(data).length;
            			       },
            					  function(errResponse){
            			    	 $scope.products=[];
            			    	 $scope.productCount=0;
            						console.error('Error while fetching product by name'+errResponse);
            					}
            	       );
                 };
      
        }]);
