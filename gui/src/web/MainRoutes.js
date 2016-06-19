application.config(['$routeProvider', '$locationProvider', '$translateProvider',
    function ($routeProvider, $locationProvider, $translateProvider) {
        $routeProvider
                .when('/', {
                    templateUrl: 'web/home/home.html',
                    controller: 'HomeController'
                })
                .when('/apartments', {
                    templateUrl: 'web/apartments/apartments.html',
                    controller: 'ApartmentsController'
                })
                .when('/location', {
                    templateUrl: 'web/location/location.html',
                    controller: 'LocationController'
                })
                .when('/contact', {
                    templateUrl: 'web/contact/contact.html',
                    controller: 'ContactController'
                })
                .when('/surroundings', {
                    templateUrl: 'web/surroundings/surroundings.html',
                    controller: 'SurroundingsController'
                })
                .when('/boat', {
                    templateUrl: 'web/boat/boat.html',
                    controller: 'BoatController'
                })
                .when('/survey', {
                templateUrl: 'web/survey/survey.html',
                controller: 'SurveyController'
            })
                .otherwise({
                    redirectTo: '/'
                });

        $locationProvider.html5Mode(true);

        $translateProvider.useStaticFilesLoader({
            prefix: 'translations/',
            suffix: '.json'
        });
        $translateProvider.preferredLanguage('en');
        $translateProvider.fallbackLanguage('en');
    }
]);