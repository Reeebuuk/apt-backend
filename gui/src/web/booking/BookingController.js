bookingModule.controller('BookingController', ['$scope', 'BookingFactory', '$q', '$location',
    function ($scope, BookingFactory, $q, $location) {

        $scope.showCalendars = false;

        function getBookedDays() {
            var deferred = $q.defer();
            var promise = deferred.promise;

            deferred.resolve(BookingFactory.getBookedDays($scope.booking.apartmentId));

            promise.then(function (data) {
                    $scope.bookedDays = data;
                    $scope.showCalendars = true;
                },
                function () {
                    console.log("error")
                });

        }

        var oneDay = 24 * 60 * 60 * 1000;

        function calculateDuration(from, to) {
            return Math.round(Math.abs((from - to) / (oneDay)));
        }

        $scope.clearFields = function () {
            var from, to;
            if ($location.path() == "/") {
                from = $scope.$parent.$parent.$parent.result.fromDate;
                to = $scope.$parent.$parent.$parent.result.toDate;
            }
            else {
                from = new Date();
                to = new Date();
            }

            $scope.booking = {
                apartmentId: $scope.$parent.app.apartmentId,
                name: "",
                surname: "",
                email: "",
                phoneNumber: "",
                address: "",
                city: "",
                country: "",
                animals: "",
                noOfPeople: "",
                note: "",
                dateFrom: from,
                dateTo: to,
                depositPaid: false
            };
            getBookedDays();

            $scope.duration = calculateDuration($scope.booking.dateFrom, $scope.booking.dateTo);
        };

        $scope.$watch('booking.dateFrom', function (newVal, oldVal) {
            $scope.duration = calculateDuration(newVal, $scope.booking.dateTo);
        }, true);

        $scope.$watch('booking.dateTo', function (newVal, oldVal) {
            $scope.duration = calculateDuration($scope.booking.dateFrom, newVal);
        }, true);

        $scope.sendBooking = function () {
            $scope.error = false;
            $scope.success = false;
            if ($scope.booking.email != "") {
                $scope.sending = true;
                var deferred = $q.defer();
                var promise = deferred.promise;

                $scope.booking.dateFrom = $scope.booking.dateFrom.getTime();
                $scope.booking.dateTo = $scope.booking.dateTo.getTime();
                deferred.resolve(BookingFactory.sendBooking($scope.booking));

                promise.then(function () {
                        $scope.sending = false;
                        $scope.success = true;
                        $scope.clearFields();
                    },
                    function () {
                        $scope.sending = false;
                        $scope.error = true;
                    });
            }
        };

        $scope.clearFields();

        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.clear = function () {
            $scope.dt = null;
        };

        $scope.calendar = {
            fromOpened: false,
            toOpened: false
        };

        $scope.open = function ($event, fromTo) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.calendar.fromOpened = false;
            $scope.calendar.toOpened = false;
            if (fromTo == 'from') {
                $scope.calendar.fromOpened = true;
            }
            if (fromTo == 'to') {
                $scope.calendar.toOpened = true;
            }
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1,
            maxMode: 'day',
            minMode: 'day'
        };

        $scope.getDayFromClass = function (date) {
            return BookingFactory.calculateDateFromStyle(date, $scope.bookedDays);
        };

        $scope.getDayToClass = function (date) {
            return BookingFactory.calculateDateToStyle(date, $scope.booking.dateFrom, $scope.bookedDays)
        };

        $scope.getDateDisabled = function (date) {
            return BookingFactory.calculateDateDisabledStyle(date, $scope.bookedDays)
        };
    }
]);