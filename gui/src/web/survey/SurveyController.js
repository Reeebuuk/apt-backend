surveyModule.controller('SurveyController', ['$scope', 'LanguageFactory', '$rootScope', '$window',
    function ($scope, LanguageFactory, $rootScope, $window) {

        $scope.currentLanguage = LanguageFactory.getCurrentLanguage().class;

        $rootScope.$on('$translateChangeSuccess', function (event, arg) {
            $scope.currentLanguage = LanguageFactory.getCurrentLanguage().class;
            resize();
        });

        angular.element($window).on('resize', function () {
            resize();
        });

        function resize() {
            var windowHeight = document.querySelector("html").offsetHeight;
            var windowWidth = document.querySelector("html").offsetWidth;
            var element = $('#' + attrs.id);
            element.css('height', windowHeight + "px");
            element.css('width', windowWidth + "px");
        }

    }
]);