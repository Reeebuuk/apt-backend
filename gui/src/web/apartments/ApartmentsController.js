apartmentsModule.controller('ApartmentsController', ['$scope', 'ApartmentsFactory', '$q', '$timeout', '$location', 'PricingFactory', '$rootScope', 'BookingFactory',
    function ($scope, ApartmentsFactory, $q, $timeout, $location, PricingFactory, $rootScope, BookingFactory) {

        var unselectedValue = 12;
        var availableApp;
        $scope.showCalendars = false;

        $scope.$on('available_apartments', function (event, arg) {
            availableApp = arg.split(',');
            $scope.apartments = ApartmentsFactory.getAvailableApartments(availableApp);
            $scope.selected = unselectedValue;
        });

        $scope.$on('show_all_apartments', function (event, arg) {
            $scope.apartments = ApartmentsFactory.getAllApartments();
            $scope.selected = unselectedValue;
        });

        $scope.isApartmentsPage = function () {
            return $location.path() == "/apartments"
        };

        $rootScope.$on('apartments_translated', function (event, arg) {
            loadApartments();
            $scope.selected = unselectedValue;
        });

        function loadApartments() {
            if ($scope.isApartmentsPage()) {
                $scope.apartments = ApartmentsFactory.getAllApartments();
                $scope.selected = unselectedValue;
            }
            else {
                $scope.apartments = ApartmentsFactory.getAvailableApartments(availableApp);
            }
        }

        loadApartments();

        $scope.showBooking = unselectedValue;

        $scope.scrollToAptDetails = function (id) {
            $timeout(function () {
                $("html, body").animate({scrollTop: $('#' + id).offset().top}, 500);
            }, 100);
        };

        $scope.setSelected = function (index) {
            if (index == $scope.selected) {
                $scope.selected = unselectedValue
            }
            else {
                $scope.showCalendars = false;
                getBookedDays($scope.apartments[index].apartmentId);
                $scope.selected = index;
                if (!$scope.isApartmentsPage()) {
                    getPricingForRange($scope.apartments[index].apartmentId);
                }
                $scope.showBooking = unselectedValue;
                $timeout(function () {
                    $scope.scrollToAptDetails('app' + index)
                }, 50);
            }
        };

        $scope.dateOptions = {
            numberOfMonths: 3,
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.dt = new Date();

        function getBookedDays(apartmentId) {
            var deferred = $q.defer();
            var promise = deferred.promise;

            deferred.resolve(BookingFactory.getBookedDays(apartmentId));

            promise.then(function (data) {
                    $scope.bookedDays = data;
                    $scope.showCalendars = true;
                },
                function () {
                    console.log("error")
                });
        }

        $scope.getDayFromClass = function (date) {
            return BookingFactory.calculateDateFromStyle(date, $scope.bookedDays);
        };

        $scope.openBooking = function (index) {
            $scope.showBooking = index;
            $timeout(function () {
                $scope.scrollToAptDetails('bookingForm')
            }, 50);
        };

        function getPricing() {
            var deferred = $q.defer();
            var promise = deferred.promise;

            deferred.resolve(PricingFactory.getPricingList());

            promise.then(function (data) {
                $scope.pricing = data;
            });
        }

        function getPricingForRange(apartmentId) {
            var deferred = $q.defer();
            var promise = deferred.promise;

            deferred.resolve(PricingFactory.getPriceForRange(apartmentId, $scope.$parent.result.fromDate, $scope.$parent.result.toDate));

            promise.then(function (data) {
                $scope.apartments[$scope.selected].price = data.price;
            });
        }

        getPricing();
    }
]);