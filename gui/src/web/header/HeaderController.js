headerModule.controller('HeaderController', ['$scope', '$location', '$translate', '$rootScope', 'PictureSizeFactory', '$window',
    'LanguageFactory',
    function ($scope, $location, $translate, $rootScope, PictureSizeFactory, $window, LanguageFactory) {

        angular.element($window).on('resize', function () {
            PictureSizeFactory.calculateAppropriateImageSizeForScreenWidth($window.innerWidth)
        });

        PictureSizeFactory.calculateAppropriateImageSizeForScreenWidth($window.innerWidth);

        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };

        $scope.selectedLanguage = LanguageFactory.getCurrentLanguage();

        $scope.languages = LanguageFactory.getAllLanguages();

        $scope.changeLanguage = function (lang) {
            LanguageFactory.setCurrentLanguage(lang);
            $scope.selectedLanguage = lang;
        };

        $scope.status = {
            isOpen: false
        };

        $scope.toggleDropdown = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.status.isOpen = !$scope.status.isOpen;
        };
    }
]);