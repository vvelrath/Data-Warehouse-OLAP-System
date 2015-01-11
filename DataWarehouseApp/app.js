'use strict';

// Declare app level module which depends on views, and components
(function(){
    var app = angular.module('myApp', []);
    
    app.directive('repeatDone', function() {
		return function(scope, element, attrs) {
			if (scope.$last) { // all are rendered
				scope.$eval(attrs.repeatDone);
			}
		};
    });

    app.controller("TabController", function() {
        this.tab = 1;

        this.isSet = function(checkTab) {
            return this.tab === checkTab;
        };

        this.setTab = function(setTab) {
            this.tab = setTab;
        };
    });
    
    app.controller("simpleCtrl", function ($scope,$http) {

        $scope.setLoading = function(loading) {
                $scope.isLoading = loading;
        };

        $scope.invokeQuery1 = function() 
         {
            $scope.setLoading(true); 
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query1Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false);
                console.log(data);
                $scope.resultSet=data;
            });
         };
         
        $scope.invokeQuery2 = function() 
         {
            $scope.setLoading(true); 
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query2Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.tab
            }).success(function (data) 
            {
                $scope.setLoading(false);
                console.log(data);
                $scope.resultSet=data;
            });
         };
         
        $scope.invokeQuery3 = function() 
         {
            $scope.setLoading(true); 
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query3Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false); 
                console.log(data);
                $scope.resultSet=data;
            });
         };

        $scope.invokeQuery4 = function() 
         {
            $scope.setLoading(true); 
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query4Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false); 
                console.log(data);
                $scope.resultSet=data;
            });
         };
         
        $scope.invokeQuery5 = function() 
         {
            $scope.setLoading(true);              
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query5Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false); 
                console.log(data);
                $scope.resultSet=data;
            });
         };

        $scope.invokeQuery6 = function() 
         {
            $scope.setLoading(true);              
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query6Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false);
                console.log(data);
                $scope.resultSet=data;
            });
         };

        $scope.invokeQuery7 = function() 
         {
            $scope.setLoading(true);              
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query7Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false);
                console.log(data);
                $scope.resultSet=data;
            });
         };

        $scope.invokeQuery8 = function() 
         {
            $scope.setLoading(true);              
            $http({
                method: 'POST',
                url: 'http://localhost:8080/Query/Query8Servlet',
                headers: {'Content-Type': 'application/json'},
                data:  $scope.user
            }).success(function (data) 
            {
                $scope.setLoading(false);
                console.log(data);
                $scope.resultSet=data;
            });
         };
 
         $scope.invokeQuery1();
     });
})();    
