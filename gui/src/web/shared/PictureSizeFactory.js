sharedModule.factory('PictureSizeFactory', [function () {
    var maxFullscreenImageWidth = 0;
    var maxTumbnailImageWidth = 0;
    var fullScreenPicturesWidths = [
        {
            full: 345,
            tumb: 345
        },
        {
            full: 500,
            tumb: 345
        }
        , {
            full: 640,
            tumb: 345
        },
        {
            full: 800,
            tumb: 345
        }
        , {
            full: 1024,
            tumb: 345
        },
        {
            full: 1280,
            tumb: 500
        }, {
            full: 1600,
            tumb: 640
        }, {
            full: 1920,
            tumb: 1024
        }];

    function calculateAppropriateImageSize(screenWidth) {
        for (var i = 0; i < fullScreenPicturesWidths.length; i++) {
            if (screenWidth <= fullScreenPicturesWidths[i].full && maxFullscreenImageWidth < fullScreenPicturesWidths[i].full) {
                maxFullscreenImageWidth = fullScreenPicturesWidths[i].full;
                maxTumbnailImageWidth = fullScreenPicturesWidths[i].tumb;
                break;
            }
        }

        if (maxFullscreenImageWidth == 0) {
            maxFullscreenImageWidth = fullScreenPicturesWidths[fullScreenPicturesWidths.length - 1].full;
            maxTumbnailImageWidth = fullScreenPicturesWidths[fullScreenPicturesWidths.length - 1].tumb;
        }
    }

    return {
        calculateAppropriateImageSizeForScreenWidth: function (screenWidth) {
            return calculateAppropriateImageSize(screenWidth);
        },
        getFullscreenWidth: function() {
            return maxFullscreenImageWidth
        },
        getTumbnailWidth: function() {
            return maxTumbnailImageWidth
        }
    };
}]);

