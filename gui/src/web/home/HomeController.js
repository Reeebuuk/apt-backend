homeModule.controller('HomeController', ['ApartmentsFactory', '$scope', '$interval', '$q', '$rootScope', '$timeout',
    function (ApartmentsFactory, $scope, $interval, $q, $rootScope, $timeout) {

        var endOfTheYearDate = new Date();
        endOfTheYearDate.setMonth(11);
        endOfTheYearDate.setDate(31);
        $scope.months = [];
        $scope.showSlider = true;

        function refreshMonths() {
            $scope.months = [];

            for (var i = 0; i < 12; i++) {
                var month = new Date();
                month.setMonth(i);
                month.setDate(1);
                $scope.months[i] = month;
            }
        }

        refreshMonths();

        var intervalPromise = $interval(callAtInterval, 500);


        function callAtInterval() {
            var selected = parseInt(angular.element(document.querySelector('#selected')).attr('number'));
            if ($scope.selectedMonths[3] != selected) {
                monthSelected(selected);
            }
        }

        $scope.$on('$destroy', function () {
            $interval.cancel(intervalPromise);
        });


        $scope.selectedMonths = [5, 6, 7, 5];

        function getStartingMonthTime() {
            return $scope.months[$scope.selectedMonths[0]].getTime()
        }

        function getEndingMonthTime() {
            var endDate;
            if ($scope.months[$scope.selectedMonths[2]].getMonth() == 11) {
                endDate = new Date($scope.months[$scope.selectedMonths[2]].getTime());
                endDate.setDate(31);
            }
            else {
                endDate = new Date($scope.months[$scope.selectedMonths[2] + 1].getTime());
                endDate.setDate(0);
            }
            return endDate.getTime()
        }

        function monthSelected(index) {

            if (index == 0) {
                $scope.selectedMonths = [0, 1, 2, 0];
            }
            else if (index == 10) {
                $scope.selectedMonths = [9, 10, 11, 11];

            }
            else {
                $scope.selectedMonths = [index, index + 1, index + 2, index];
            }
            recalculateRange();
            recalibrateDefaultDate();
            calculateDuration();
        }

        function recalculateRange() {
            $scope.difference = getEndingMonthTime() - getStartingMonthTime();
        }

        var oneDay = 24 * 60 * 60 * 1000;

        var minRange = 3 * oneDay;

        var noOfSteps = 30;
        var stepSize = 10000000;

        $scope.onTimeout = function () {
            if (noOfSteps > 0) {
                noOfSteps--;

                $scope.result = {
                    from: $scope.result.from + stepSize,
                    to: $scope.result.to - stepSize,
                    fromDate: new Date($scope.result.fromDate + stepSize),
                    toDate: new Date($scope.result.toDate - stepSize),
                    minRange: minRange
                };
            }
            animationTimeout = $timeout($scope.onTimeout, 10);
        };


        function recalibrateDefaultDate() {
            var initialFrom = new Date($scope.months[$scope.selectedMonths[1]].getTime());
            initialFrom.setDate(13);

            var initialTo = new Date($scope.months[$scope.selectedMonths[1]].getTime());
            initialTo.setDate(20);

            var animationFrom = new Date(initialFrom.getTime() - noOfSteps * stepSize);
            var animationTo = new Date(initialTo.getTime() + noOfSteps * stepSize);

            $scope.result = {
                from: animationFrom.getTime() - getStartingMonthTime(),
                to: animationTo.getTime() - getStartingMonthTime(),
                fromDate: animationFrom,
                toDate: animationTo,
                minRange: minRange
            };
        }

        recalculateRange();
        recalibrateDefaultDate();

        function calculateDuration() {
            if ($scope.result.toDate != null && $scope.result.fromDate != null) {
                $scope.duration = Math.round(Math.abs(($scope.result.toDate.getTime() - $scope.result.fromDate.getTime()) / (oneDay)));

                var deferred = $q.defer();
                var promise = deferred.promise;

                deferred.resolve(ApartmentsFactory.getAvailableApartmentsForRange($scope.result.fromDate, $scope.result.toDate));

                promise.then(function (data) {
                    var availableApartments = [1, 2, 3];

                    $scope.apartmentsAvailable = availableApartments.length - data.length;

                    for (var i = 0; i < data.length; i++) {
                        availableApartments.remove(parseInt(data[i].apartmentId));
                    }
                    $rootScope.$broadcast('available_apartments', availableApartments.join(','));
                });
            }
        }

        $scope.$watch('result.from', function (newVal, oldVal) {
            $scope.result.fromDate.setTime(newVal + getStartingMonthTime());
            calculateTimeout();
        }, true);

        $scope.$watch('result.to', function (newVal, oldVal) {
            $scope.result.toDate.setTime(newVal + getStartingMonthTime());
            calculateTimeout();
        }, true);

        var sliderTimeout;

        function calculateTimeout() {
            var timeoutTime = 200;

            if (sliderTimeout != null) {
                $timeout.cancel(sliderTimeout);
            }
            sliderTimeout = $timeout(calculateDuration, timeoutTime);
        }

        $scope.scrollToApartments = function () {
            $("html, body").animate({scrollTop: $('.apartments').offset().top + 20}, 500);
        };

        var animationTimeout;

        $(document).ready(function () {
            resize();
            animationTimeout = $timeout($scope.onTimeout, 1000);
        });

        function resize() {
            var offset = document.querySelector("html").offsetHeight;
            var offsetWidth = document.querySelector("html").offsetWidth;
            var mainContent = $('.main-content');
            mainContent.css('min-height', offset);
            mainContent.css('background-size', offsetWidth + "px " + offset + "px");
            var marginTop = 52;
            if (offset > 529) {
                marginTop = marginTop + (offset - 529) / 2;
            }
            $('.slider-header').css('margin-top', marginTop);
        }

        var resizeTimer;
        $(window).resize(function () {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(resize, 100);
        });


        $rootScope.$on('$translateChangeSuccess', function (event, arg) {
            $scope.showSlider = false;
            refreshMonths();
            $timeout($scope.showIt, 100);
        });

        $scope.showIt = function () {
            $scope.showSlider = true;
        }
    }
]);
