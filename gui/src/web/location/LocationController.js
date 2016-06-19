locationModule.controller('LocationController', ['$scope', 'LanguageFactory', '$rootScope',
    function ($scope, LanguageFactory, $rootScope) {

        var currentLanguage = LanguageFactory.getCurrentLanguage().class;

        $rootScope.$on('$translateChangeSuccess', function (event, arg) {
            currentLanguage = LanguageFactory.getCurrentLanguage().class;
            map = new google.maps.Map(document.getElementById("map"), myOptions);
        });

        var myOptions = {
            center: new google.maps.LatLng(43.9090643, 15.509069400000044),
            zoom: 10,
            scrollwheel: false,
            navigationControl: true,
            mapTypeControl: true,
            scaleControl: true,
            disableDoubleClickZoom: true,
            streetViewControl: true,
            mapTypeId: google.maps.MapTypeId.TERRAIN,
            language: currentLanguage
        };

        var map = new google.maps.Map(document.getElementById("map"), myOptions);

        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(43.913165, 15.508193),
            map: map,
            icon: "assets/img/villa.png",
            title: "Zadarska 13, 23211, Pakoštane"
        });
    }
]);