'use strict';

var homeModule = angular.module('Home', []);
var locationModule = angular.module('Location', []);
var surveyModule = angular.module('Survey', []);
var surroundingsModule = angular.module('Surroundings', ['Shared']);
var boatModule = angular.module('Boat', ['Shared']);
var sharedModule = angular.module('Shared', []);
var apartmentsModule = angular.module('Apartments', ['Shared']);
var headerModule = angular.module('Header', []);
var contactModule = angular.module('Contact', ['Shared']);
var bookingModule = angular.module('Booking', ['Shared', 'Apartments']);

var application = angular.module('AptWeb', [
    'ngRoute',
    'ngCookies',
    'Home',
    'Apartments',
    'Header',
    'Contact',
    'Booking',
    'Surroundings',
    'Shared',
    'ngScrollSpy',
    'pascalprecht.translate',
    'ui.bootstrap.datepicker',
    'ui.bootstrap.dropdown',
    'ui-rangeSlider',
    'angulartics',
    'angulartics.google.analytics',
    'Location',
    'Survey',
    'Boat'
]);

application.run([
    function ()
    {
        Array.prototype.remove = function() {
            var what, a = arguments, L = a.length, ax;
            while (L && this.length) {
                what = a[--L];
                while ((ax = this.indexOf(what)) !== -1) {
                    this.splice(ax, 1);
                }
            }
            return this;
        };
    }]);

