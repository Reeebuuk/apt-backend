boatModule.factory('BoatFactory', ['PictureSizeFactory',
    function (PictureSizeFactory) {
        var fullscreenWidth = PictureSizeFactory.getFullscreenWidth();
        var tumbnailWidth = PictureSizeFactory.getTumbnailWidth();

        function showBoat() {
            return {
                previewImages: [
                    {
                        big: 'boat/' + fullscreenWidth + '/brod1.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod1.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1152',
                        show: 'true'
                    },                    {
                        big: 'boat/' + fullscreenWidth + '/brod2.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod2.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1152',
                        show: 'false'
                    },                    {
                        big: 'boat/' + fullscreenWidth + '/brod3.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod3.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1152',
                        show: 'true'
                    },                    {
                        big: 'boat/' + fullscreenWidth + '/brod4.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod4.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1152',
                        show: 'true'
                    },                    {
                        big: 'boat/' + fullscreenWidth + '/brod5.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod5.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1434',
                        show: 'false'
                    },                    {
                        big: 'boat/' + fullscreenWidth + '/brod6.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod6.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1434',
                        show: 'false'
                    },                    {
                        big: 'boat/' + fullscreenWidth + '/brod7.jpeg',
                        small: 'boat/' + tumbnailWidth + '/brod7.jpeg',
                        title: 'Boat',
                        width: '1920',
                        height: '1434',
                        show: 'false'
                    }
                ]
            }

        }

        return {
            getBoat: function () {
                return showBoat();
            }
        };
    }
]);

