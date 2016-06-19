surroundingsModule.controller('SurroundingsController', ['$scope', 'SurroundingsFactory', '$window', '$timeout', '$rootScope',
    function ($scope, SurroundingsFactory, $window, $timeout, $rootScope) {

        var unselectedValue = 12;

        $scope.setSelected = function (index) {
            if (index == $scope.selectedItem) {
                $scope.selectedItem = unselectedValue
            }
            else {
                $scope.selectedItem = index;
            }
            $timeout(function () {
                $scope.scrollToDetails('item' + index)
            }, 50);
        };

        $scope.scrollToDetails = function (id) {
            $timeout(function () {
                $("html, body").animate({scrollTop: $('#' + id).offset().top}, 500);
            }, 100);
        };

        $scope.openLink = function (link) {
            $window.open(link);
        };

        $scope.selectedItem = unselectedValue;
        $scope.radioModel = 'beaches';

        $scope.selected = {
            beaches: true,
            cities: false,
            nature: false
        };

        $scope.show = function (showing) {
            $scope.radioModel = showing;
            $scope.selectedItem = unselectedValue;
            if (showing == 'beaches') {
                $scope.selected = {
                    beaches: true,
                    cities: false,
                    nature: false
                };
                $scope.list = SurroundingsFactory.getBeaches();
            }
            if (showing == 'cities') {
                $scope.selected = {
                    beaches: false,
                    cities: true,
                    nature: false
                };
                $scope.list = SurroundingsFactory.getCities();
            }
            if (showing == 'nature') {
                $scope.selected = {
                    beaches: false,
                    cities: false,
                    nature: true
                };
                $scope.list = SurroundingsFactory.getNature();
            }
        };

        $scope.show("beaches");

        $rootScope.$on('surroundings_translated', function (event, arg) {
            $scope.show($scope.radioModel);
        });
    }
]);