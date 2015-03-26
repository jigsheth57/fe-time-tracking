'use strict';

var timeEntryApp = angular.module('timeEntryApp', [], function($locationProvider) {
	$locationProvider.html5Mode(true);
});

// Controller for getting all entries
timeEntryApp.controller('EntryListController', function($scope, $http) {
	$http.get('/entries/').success(function(data) {
		$scope.entries = data;
	});
});

// Controller for getting an entry detail
timeEntryApp.controller('EntryDetailController', function($scope, $http, $location) {
	$http.get('/entries/' + $location.search()['id']).success(function(data) {
		$scope.entry = data;
	});
});




