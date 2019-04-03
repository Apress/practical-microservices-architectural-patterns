'use strict';

ecomApp.controller('NewUserController', ['$scope','$location','UserService', function($scope,$location,UserService) {
	
	 var userCtrl=this;
	 $scope.user={};
	 $scope.user.sameBillingAddress=true;
	 $scope.shipping={};
	 $scope.billing={};
	 $scope.passwordNotMaching=false;
	 $scope.duplicateUser=false;
	 userCtrl.addUser =function(){
		 $scope.passwordNotMaching=false;
		 $scope.duplicateUser=false;
		 if($scope.user.password!=$scope.user.confirmPassword){
			 $scope.passwordNotMaching=true;
			 return;
		 } else{
			 if($scope.user.sameBillingAddress){
				 $scope.shipping=$scope.billing;
			 }
			var customerDTO ={
					userName:$scope.user.userName,
					firstName:$scope.user.firstName,
					lastName: $scope.user.lastName,
				    email :$scope.user.email,
					phone:$scope.user.phone,
					password:$scope.user.password,
					billingAddress:$scope.billing,
					shippingAddress:$scope.shipping
			};
			 
			 
			 UserService.addNewuser(customerDTO)
             .then(
    			       function(data) {
    			    	  if(String(data.content)=='SUCCESS'){
    			    		  $location.url('/user');
    			    	  }else{
    			    		  $scope.duplicateUser=true;  
    			    	  }
    			        },
    					  function(errResponse){
    					 console.error('Error while adding new user'+errResponse);
    					}
    	       );
			 
		 }
	 };
     

}]);

ecomApp.controller('LoginController', ['$scope','$location','UserService','CartService', function($scope,$location,UserService,CartService) {
	
	 var loginCtrl=this;
	 $scope.userCredentialDTO={};
	 $scope.validuser=true;;
	 loginCtrl.doLogin =function(){				 
			 UserService.validateUser($scope.userCredentialDTO)
            .then(
   			       function(data) {
   			    	 sessionStorage.setItem('loggedUser',JSON.stringify(data));
 			    	 sessionStorage.setItem('sessionUser',data.userId);
 			    	 $scope.screenName=data.firstName+ '  '+data.lastName;
 			    	 $scope.$emit('userModified', $scope.screenName);
 			    	 if($scope.myCart!=null){
 			    	   $scope.myCart.userId=data.userId;
 			    	 }
   			    	
 			    	 loginCtrl.loadCart(); 
 			    	 $location.url('/order'); 
   		        },
   					  function(errResponse){
   			        	$scope.validuser=false;
   					    console.error('Error while validating user user'+errResponse);
   					}
   			        
   	       );
			 
		 };
		 
		 loginCtrl.loadCart =function(){
			 CartService.getUserCart(sessionStorage.getItem('sessionUser'))
                .then(
				       function(data) {
				    	   if(data.lineItems!=null){
				    		 if($scope.myCart.lineItems==null){
				    			 $scope.myCart.lineItems= [];
				    		 }
				    		 $scope.myCart.lineItems=$scope.myCart.lineItems.concat(data.lineItems);
				    	   }
				    	 CartService.updateUserCart($scope.myCart)
		                 .then(
		            		  function(data) {
		            			  $scope.$emit('cartModified', $scope.myCart);
		            			 
		            		  },
		            		  function(errResponse){
						    	   console.error('Error while updating user cache '+errResponse);
		  					  }
		                  );
				       },
				      function(errResponse){
			    	   console.error('Error while getting  user  from cache '+errResponse);
					  }
             ); 
			 
		 
		 }
    

}]);
