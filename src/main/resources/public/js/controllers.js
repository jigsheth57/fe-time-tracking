/*
 * JS file for all of the Angular controllers in the app
 */
'use strict';

/* 
 * Define the TimeEntry app scope for Angular
 */
var timeEntryApp = angular.module('timeEntryApp', [], function($locationProvider) {
	$locationProvider.html5Mode(true);
});

/*
 * Controller for getting all entries. This controller makes an ajax call to the 
 * TimeEntry REST controller (/entries/) to get all of the entries, and uses
 * the results for the model.
 */ 
timeEntryApp.controller('EntryListController', function($scope, $http) {
	
	//Handles the delete request function
	$scope.delete = function(entryId) {
		console.log("deleting id " + entryId);
		$http.delete("/entries/delete/" + entryId).success(function(data, status, headers, config) {
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
			$scope.entries = data;
		})
	};

	//Initial page load
	$scope.getEntries();
});

/*
 * Controller for editing a time entry.  This controller handles both the initial
 * display of the entry, by making an ajax request to the TimeEntry REST controller
 * (/entries/:id), and also handle the form submission by making an ajax post request
 * to the TimeEntry REST controller (/entries/save) with the form data.
 */ 
timeEntryApp.controller('EditEntryController', function($scope, $http, $location) {
	
	//Handles the update request function
	$scope.update = function(entry) {
		$http.post("/entries/save", entry).success(function(data, status, headers, config) {
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
		$http.get('/entries/' + $location.search()['id']).success(function(data) {
			$scope.entry = data;
			$scope.message = "";
			$scope.error = "";
		});
	}
	
	//Initial load
	$scope.reset();
});



