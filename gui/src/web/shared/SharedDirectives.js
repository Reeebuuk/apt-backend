sharedModule.directive("ngElementReady", function () {
    return function (scope, element, attrs) {

        if (attrs.number == attrs.size-1)
        {

                $('#' + attrs.parentId).anoSlide(
                    {
                        items: 3,
                        speed: 500,
                        prev: 'a.prev[data-prev]',
                        next: 'a.next[data-next]',
                        lazy: false,
                        delay: 0,
                        initial: 5
                    });
        }
    }
});

sharedModule.directive("photoSwipeReady", function () {
    return function (scope, element, attrs) {

        if (attrs.number == attrs.size-1)
        {
        initPhotoSwipeFromDOM('.my-simple-gallery');
        }

    }
});

sharedModule.directive("resizeReady", function () {
    return function (scope, element, attrs) {
        var windowHeight = document.querySelector("html").offsetHeight;
        var windowWidth = document.querySelector("html").offsetWidth;
        var element = $('#' + attrs.id);
        element.css('height', windowHeight + "px");
        element.css('width', windowWidth + "px");
    }
});


sharedModule.directive('jmDpRefreshView', function () {
    var noop = function () {
    };
    var refreshDpOnNotify = function (dpCtrl) {
        return function () {
            dpCtrl.refreshView();
        };
    };
    return {
        require: 'datepicker',
        link: function (scope, elem, attrs, dpCtrl) {
            var refreshPromise = scope[attrs.jmDpRefreshView];
            refreshPromise.then(noop, noop, refreshDpOnNotify(dpCtrl));
        }
    };
});