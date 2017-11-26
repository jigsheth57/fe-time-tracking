var timeEntryApp = angular.module('timeEntryApp', ['ngRoute', 'swxSessionStorage', 'timeEntryControllers']);

timeEntryApp.config(['$routeProvider', 
    function($routeProvider) {
		$routeProvider.when('/entries', {
			templateUrl: '/partials/entryList.html',
			controller: 'EntryListController'
		}).when('/entries/:entryId', {
			templateUrl: '/partials/editEntry.html',
			controller: 'EditEntryController'
		}).otherwise({
			redirectTo: '/entries'
		})
}])