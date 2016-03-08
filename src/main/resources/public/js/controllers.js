/*
 * JS file for all of the Angular controllers in the app
 */
'use strict';

/* 
 * Define the TimeEntry controllers scope for Angular
 */
var timeEntryControllers = angular.module('timeEntryControllers', []);
timeEntryApp.$inject = ['$sessionStorage'];
/*
 * Controller for getting all entries. This controller makes an ajax call to the 
 * TimeEntry REST controller (/entries/) to get all of the entries, and uses
 * the results for the model.
 */ 
timeEntryApp.controller('EntryListController', function($scope, $sessionStorage, $http) {
	
	//Handles the delete request function
	$scope.delete = function(entryId) {
		console.log("deleting id " + entryId);
		$http.delete("/entries/" + entryId).success(function(data, status, headers, config) {
			$scope.getEntries();
			$scope.message = "Successfully deleted the entry.";
			$scope.error = ""
		}).error(function(data, status, headers, config) {
			$scope.message = "";
			$scope.error = "There was an error deleting the entry.";
		});
	};
	
	//Reloads the data
	$scope.getEntries = function() {
		$http.get('/entries/').success(function(data) {
			console.log("response data: "+JSON.stringify(data));
			//console.log("employees data: "+data._embedded);
			//console.log("employees response data: "+JSON.stringify(data._embedded));
			$scope.entries = data;
		})
	};

    $scope.login = function() {
    	var callbackURL = window.location.origin + "/oauth_secure.html";
    	var oauth2URL = "https://uaa.west-1.fe.gopivotal.com/oauth/authorize?response_type=token&client_id=portal&redirect_uri="+callbackURL;
    	//console.log(oauth2URL);
    	$sessionStorage.put('uaa',window.sessionStorage.getItem('uaa'),1/72);
    	var uaa = $sessionStorage.get('uaa');
    	console.log(uaa);
    	if(uaa) {
        	if(JSON.parse(uaa).oauth.access_token)
        		$scope.getEntries();
    	} else
    		window.location.href = oauth2URL;
    	//window.sessionStorage.removeItem('uaa');
    }
	//Initial page load
	$scope.getEntries();
});

/*
 * Controller for editing a time entry.  This controller handles both the initial
 * display of the entry, by making an ajax request to the TimeEntry REST controller
 * (/entries/:id), and also handle the form submission by making an ajax post request
 * to the TimeEntry REST controller (/entries/save) with the form data.
 */ 
timeEntryApp.controller('EditEntryController', function($scope, $http, $routeParams) {
	
	//Handles the update request function
	$scope.update = function(entry) {
		$http.post("/entries/", entry).success(function(data, status, headers, config) {
			$scope.entry = data;
			$scope.message = "Successfully saved the entry.";
			$scope.error = "";
		}).error(function(data, status, headers, config) {
			$scope.message = "";
			$scope.error = "There was an error saving the entry.";
		});
	};
	
	//Handles the reset request function and the initial load of the entry
	$scope.reset = function(entry) {
		$http.get('/entries/' + $routeParams.entryId).success(function(data) {
			$scope.entry = data;
			$scope.message = "";
			$scope.error = "";
		});
	}
	
	//Initial load
	$scope.reset();
});



