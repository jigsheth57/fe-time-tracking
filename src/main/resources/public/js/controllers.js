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
	$http.get('/entries/').success(function(data) {
		$scope.entries = data;
	});
});

/*
 * Controller for getting a single entry. This controller makes an ajax call to the 
 * TimeEntry REST controller (/entries/:id) to get time entry, and uses
 * the results for the model.
 */ 
timeEntryApp.controller('EntryDetailController', function($scope, $http, $location) {
	$http.get('/entries/' + $location.search()['id']).success(function(data) {
		$scope.entry = data;
	});
});




