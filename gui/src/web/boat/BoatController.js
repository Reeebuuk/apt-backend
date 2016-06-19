boatModule.controller('BoatController', ['$scope', 'LanguageFactory', '$rootScope', 'BoatFactory',
    function ($scope, LanguageFactory, $rootScope, BoatFactory) {

        var currentLanguage = LanguageFactory.getCurrentLanguage().class;

        $rootScope.$on('$translateChangeSuccess', function (event, arg) {
            currentLanguage = LanguageFactory.getCurrentLanguage().class;
        });

        $scope.boat = BoatFactory.getBoat()
    }
]);