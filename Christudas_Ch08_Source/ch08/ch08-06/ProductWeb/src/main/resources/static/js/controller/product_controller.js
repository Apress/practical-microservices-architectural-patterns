'use strict';

App.controller('ProductController', ['$scope', 'ProductService', function($scope, ProductService) {
          var self = this;
          self.products=[];
          $scope.init = function () { 
        	 self.refresh();
          
          };
          
          self.refresh = function(){ 
        	  ProductService.getApplication()
              .then(
				       function(data) {
				    	 self.products= data; 
				       },
  					function(errResponse){
  						console.error('Error while fetching applications');
  					}
		       );
          };
        }]);
