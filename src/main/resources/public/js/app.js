var timeEntryApp = angular.module('timeEntryApp', ['ngRoute', 'swxSessionStorage', 'timeEntryControllers']);

timeEntryApp.config(['$routeProvider', 
    function($routeProvider) {
		$routeProvider.when('/timeentries', {
			templateUrl: '/partials/entryList.html',
			controller: 'EntryListController'
		}).when('/timeentries/:entryId', {
			templateUrl: '/partials/editEntry.html',
			controller: 'EditEntryController'
		}).otherwise({
			redirectTo: '/timeentries'
		})
}])