surroundingsModule.factory('SurroundingsFactory', ['PictureSizeFactory', '$translate', '$rootScope',
    function (PictureSizeFactory, $translate, $rootScope) {
        var fullscreenWidth = PictureSizeFactory.getFullscreenWidth();
        var tumbnailWidth = PictureSizeFactory.getTumbnailWidth();
        var beaches, cities, nature;

        function showBeaches() {
            return beaches
        }

        function showNature() {
            return nature;
        }

        function showCities() {
            return cities
        }

        function translate(){
            beaches = [
                {
                    name: $translate.instant('beach_janice'),
                    distance: '1 km',
                    description: $translate.instant('beach_janice_description'),
                    link: null,
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ul.+13,+23211,+Pako%C5%A1tane/43.9062104,15.5093641/@43.9080145,15.5066105,1645m/data=!3m1!1e3!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e2?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_3.jpeg',
                            small: 'surroundings/beaches/Janice/' + tumbnailWidth + '/janice_1_beach_3.jpeg',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_15.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_4.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        //{
                        //    big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_5.jpeg',
                        //    small: 'zoom.png',
                        //    title: 'Beach Janice',
                        //    width: '1920',
                        //    height: '1440',
                        //    show: 'false'
                        //},
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_6.jpeg',
                            small: 'surroundings/beaches/Janice/' + tumbnailWidth + '/janice_1_beach_6.jpeg',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_7.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_8.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_9.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        //{
                        //    big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_10.jpeg',
                        //    small: 'zoom.png',
                        //    title: 'Beach Janice',
                        //    width: '1920',
                        //    height: '1440',
                        //    show: 'false'
                        //},
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_11.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_12.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_13.jpeg',
                            small: 'surroundings/beaches/Janice/' + tumbnailWidth + '/janice_1_beach_13.jpeg',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Janice/' + fullscreenWidth + '/janice_1_beach_14.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Janice',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/beaches/Janice/header.jpg'
                },
                {
                    name: $translate.instant('beach_punta'),
                    distance: '1 km',
                    description: $translate.instant('beach_punta_description'),
                    link: null,
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ul.+13,+23211,+Pako%C5%A1tane/43.9069216,15.5041992/@43.9080047,15.5093535,2017m/data=!3m1!1e3!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e2?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_1.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_2.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_3.jpeg',
                            small: 'surroundings/beaches/Punta/' + tumbnailWidth + '/punta_beach_3.jpeg',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        },
                        //{
                        //    big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_4.jpeg',
                        //    small: 'zoom.png',
                        //    title: 'Beach Punta',
                        //    width: '1920',
                        //    height: '1280',
                        //    show: 'false'
                        //},
                        //{
                        //    big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_5.jpeg',
                        //    small: 'zoom.png',
                        //    title: 'Beach Punta',
                        //    width: '1920',
                        //    height: '1280',
                        //    show: 'false'
                        //},
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_6.jpeg',
                            small: 'surroundings/beaches/Punta/' + tumbnailWidth + '/punta_beach_6.jpeg',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_7.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_8.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_9.jpeg',
                            small: 'surroundings/beaches/Punta/' + tumbnailWidth + '/punta_beach_9.jpeg',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1260',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_10.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_11.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Punta/' + fullscreenWidth + '/punta_beach_12.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Punta',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/beaches/Punta/header.jpg'
                },
                {
                    name: $translate.instant('beach_kozarica'),
                    distance: '1.5 km',
                    description: $translate.instant('beach_kozarica_description'),
                    link: null,
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ul.+13,+23211,+Pako%C5%A1tane/43.9074666,15.4992537/@43.9086885,15.4982536,2016m/data=!3m1!1e3!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e2?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_2.jpeg',
                            small: 'surroundings/beaches/Kozarica/' + tumbnailWidth + '/kozarica_beach_2.jpeg',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '1174',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_9.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_4.jpeg',
                            small: 'surroundings/beaches/Kozarica/' + tumbnailWidth + '/kozarica_beach_4.jpeg',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '1163',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_5.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_6.jpeg',
                            small: 'surroundings/beaches/Kozarica/' + tumbnailWidth + '/kozarica_beach_6.jpeg',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '1218',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_7.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_3.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_1.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '2560',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_8.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Kozarica/' + fullscreenWidth + '/kozarica_beach_10.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Kozarica',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/beaches/Kozarica/header.jpg'
                },
                {
                    name: $translate.instant('beach_buzakovina'),
                    distance: '1.5 km',
                    description: $translate.instant('beach_buzakovina_description'),
                    link: null,
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ul.+13,+23211,+Pako%C5%A1tane/43.9132545,15.4939897/@43.9140339,15.4956825,1641m/data=!3m2!1e3!4b1!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e2?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_1.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_2.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_3.jpeg',
                            small: 'surroundings/beaches/Buzakovina/' + tumbnailWidth + '/buzakovina_beach_3.jpeg',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1730',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_4.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_5.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '856',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_6.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1730',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_7.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_8.jpeg',
                            small: 'surroundings/beaches/Buzakovina/' + tumbnailWidth + '/buzakovina_beach_8.jpeg',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1730',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_9.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_10.jpeg',
                            small: 'surroundings/beaches/Buzakovina/' + tumbnailWidth + '/buzakovina_beach_10.jpeg',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1730',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_11.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_12.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_13.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Buzakovina/' + fullscreenWidth + '/buzakovina_beach_14.jpeg',
                            small: 'zoom.png',
                            title: 'Buzakovina beach',
                            width: '1920',
                            height: '658',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/beaches/Buzakovina/header.jpg'
                },
                {
                    name: $translate.instant('beach_pilatusa'),
                    distance: '2 km',
                    description: $translate.instant('beach_pilatusa_description'),
                    link: null,
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ul.+13,+23211,+Pako%C5%A1tane/43.9148826,15.48869/@43.9090312,15.496113,2021m/data=!3m1!1e3!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e2?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_1.jpeg',
                            small: 'surroundings/beaches/Pilatusa/' + tumbnailWidth + '/beach_pilatusa_1.jpeg',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_3.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1080',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_2.jpeg',
                            small: 'surroundings/beaches/Pilatusa/' + tumbnailWidth + '/beach_pilatusa_2.jpeg',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_4.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '603',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_5.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_6.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_7.jpeg',
                            small: 'surroundings/beaches/Pilatusa/' + tumbnailWidth + '/beach_pilatusa_7.jpeg',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_8.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Pilatusa/' + fullscreenWidth + '/beach_pilatusa_9.jpeg',
                            small: 'zoom.png',
                            title: 'Beach Pilatuša',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/beaches/Pilatusa/header.jpg'
                },
                {
                    name: $translate.instant('beach_nudistic'),
                    distance: '2 km',
                    description: $translate.instant('beach_nudistic_description'),
                    link: null,
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ul.+13,+23211,+Pako%C5%A1tane/43.9147662,15.4867396/@43.9081167,15.4918898,2021m/data=!3m1!1e3!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e2?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/beaches/Nudistic/' + fullscreenWidth + '/nudistic_beach_1.jpeg',
                            small: 'surroundings/beaches/Nudistic/' + tumbnailWidth + '/nudistic_beach_1.jpeg',
                            title: 'Nudistic beach',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Nudistic/' + fullscreenWidth + '/nudistic_beach_2.jpeg',
                            small: 'surroundings/beaches/Nudistic/' + tumbnailWidth + '/nudistic_beach_2.jpeg',
                            title: 'Nudistic beach',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/beaches/Nudistic/' + fullscreenWidth + '/nudistic_beach_4.jpeg',
                            small: 'zoom.png',
                            title: 'Nudistic beach',
                            width: '1920',
                            height: '1281',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/beaches/Nudistic/' + fullscreenWidth + '/nudistic_beach_3.jpeg',
                            small: 'surroundings/beaches/Nudistic/' + tumbnailWidth + '/nudistic_beach_3.jpeg',
                            title: 'Nudistic beach',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        }
                    ],
                    image: 'surroundings/beaches/Nudistic/header.jpg'
                }
            ];

            nature = [
                {
                    name: $translate.instant('vransko_name'),
                    distance: '600 m',
                    description: $translate.instant('vransko_description'),
                    link: "http://www.pp-vransko-jezero.hr/hr/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/43.9158098,15.5118796/@43.9208102,15.5159979,15.46z/data=!4m8!4m7!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-1.jpeg',
                            small: 'surroundings/nature/Vransko/' + tumbnailWidth + '/Vransko-jezer-apartments-blanka-1.jpeg',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-2.jpeg',
                            small: 'zoom.png',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-4.jpeg',
                            small: 'zoom.png',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-7.jpeg',
                            small: 'surroundings/nature/Vransko/' + tumbnailWidth + '/Vransko-jezer-apartments-blanka-7.jpeg',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-8.jpeg',
                            small: 'zoom.png',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-9.jpeg',
                            small: 'zoom.png',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-10.jpeg',
                            small: 'zoom.png',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-13.jpeg',
                            small: 'surroundings/nature/Vransko/' + tumbnailWidth + '/Vransko-jezer-apartments-blanka-13.jpeg',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }, {
                            big: 'surroundings/nature/Vransko/' + fullscreenWidth + '/Vransko-jezer-apartments-blanka-14.jpeg',
                            small: 'zoom.png',
                            title: 'Vransko jezero',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }

                    ],
                    image: 'surroundings/nature/Vransko/header.jpg'
                },
                {
                    name: $translate.instant('kamenjak_name'),
                    distance: '18 km',
                    description: $translate.instant('kamenjak_description'),
                    link: "http://www.pp-vransko-jezero.hr/hr/vidikovac-kamenjak/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/43.8948777,15.6104801/@43.9085089,15.5716202,12012m/data=!3m1!1e3!4m9!4m8!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0!3e0?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_1.jpeg',
                            small: 'surroundings/nature/Vidikovac/' + tumbnailWidth + '/vidikovac_1.jpeg',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_2.jpeg',
                            small: 'zoom.png',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_3.jpeg',
                            small: 'zoom.png',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_4.jpeg',
                            small: 'surroundings/nature/Vidikovac/' + tumbnailWidth + '/vidikovac_4.jpeg',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_5.jpeg',
                            small: 'zoom.png',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_6.jpeg',
                            small: 'zoom.png',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_7.jpeg',
                            small: 'surroundings/nature/Vidikovac/' + tumbnailWidth + '/vidikovac_7.jpeg',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }, {
                            big: 'surroundings/nature/Vidikovac/' + fullscreenWidth + '/vidikovac_8.jpeg',
                            small: 'zoom.png',
                            title: 'Vidikovac',
                            width: '1920',
                            height: '1263',
                            show: 'false'
                        }

                    ],
                    image: 'surroundings/nature/Vidikovac/header.jpg'
                },
                {
                    name: $translate.instant('national_park_kornati'),
                    distance: '45 km',
                    description: $translate.instant('national_park_kornati_description'),
                    link: "http://www.np-kornati.hr/en",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/Kornati+National+park,+Murter,+Croatia/@43.8314872,15.2980863,11z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x1334b8fb8f9e92cb:0xee81e78605203d73!2m2!1d15.383381!2d43.749553?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/1.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1534',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/2.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/3.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1251',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/4.jpeg',
                            small: 'surroundings/nature/Kornati/' + tumbnailWidth + '/4.jpeg',
                            title: 'Kornati',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/5.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1038',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/6.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1937',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/7.jpeg',
                            small: 'surroundings/nature/Kornati/' + tumbnailWidth + '/7.jpeg',
                            title: 'Kornati',
                            width: '1920',
                            height: '1274',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/8.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/9.jpeg',
                            small: 'zoom.png',
                            title: 'Kornati',
                            width: '1920',
                            height: '1920',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Kornati/' + fullscreenWidth + '/10.jpeg',
                            small: 'surroundings/nature/Kornati/' + tumbnailWidth + '/10.jpeg',
                            title: 'Kornati',
                            width: '1920',
                            height: '1274',
                            show: 'true'
                        }
                    ],
                    image: 'surroundings/nature/Kornati/header.jpg'
                },
                {
                    name: $translate.instant('national_park_krka'),
                    distance: '53 km',
                    description: $translate.instant('national_park_krka_description'),
                    link: "http://www.np-krka.hr/en/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/Krka+National+Park,+Op%C4%87ina+%C5%A0ibenik,+Croatia/@43.8777648,15.5912686,11z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x13352dceeb0b369d:0x12e1d4d5d0103938!2m2!1d15.972484!2d43.866602?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-1.jpeg',
                            small: 'zoom.png',
                            title: 'Krka',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-2.jpeg',
                            small: 'surroundings/nature/Krka/' + tumbnailWidth + '/National-park-krka-apartments-blanka-2.jpeg',
                            title: 'Krka',
                            width: '1920',
                            height: '1284',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-3.jpeg',
                            small: 'zoom.png',
                            title: 'Krka',
                            width: '1920',
                            height: '1461',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-4.jpeg',
                            small: 'zoom.png',
                            title: 'Krka',
                            width: '1920',
                            height: '1082',
                            show: 'false'
                        },
                        //{big:'surroundings/nature/Krka/'+fullscreenWidth+'/National-park-krka-apartments-blanka-5.jpeg', small:'zoom.png', title:'Krka', width:'1920', height:'1437', show:'false'},
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-6.jpeg',
                            small: 'surroundings/nature/Krka/' + tumbnailWidth + '/National-park-krka-apartments-blanka-6.jpeg',
                            title: 'Krka',
                            width: '1920',
                            height: '1277',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-7.jpeg',
                            small: 'zoom.png',
                            title: 'Krka',
                            width: '1920',
                            height: '1080',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-8.jpeg',
                            small: 'zoom.png',
                            title: 'Krka',
                            width: '1920',
                            height: '1285',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-9.jpeg',
                            small: 'zoom.png',
                            title: 'Krka',
                            width: '1920',
                            height: '1353',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Krka/' + fullscreenWidth + '/National-park-krka-apartments-blanka-10.jpeg',
                            small: 'surroundings/nature/Krka/' + tumbnailWidth + '/National-park-krka-apartments-blanka-10.jpeg',
                            title: 'Krka',
                            width: '1920',
                            height: '1277',
                            show: 'true'
                        }
                    ],
                    image: 'surroundings/nature/Krka/header.jpg'
                },
                {
                    name: $translate.instant('national_park_paklenica'),
                    distance: '63 km',
                    description: $translate.instant('national_park_paklenica_description'),
                    link: "http://www.np-paklenica.hr/en/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/Paklenica+National+Park,+Starigrad,+Croatia/@44.1281636,15.353608,11z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x476191109568e00d:0xb2963721bdd928e4!2m2!1d15.483997!2d44.343707?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/1.jpeg',
                            small: 'surroundings/nature/Paklenica/' + tumbnailWidth + '/1.jpeg',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1388',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/2.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1080',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/3.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '2560',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/4.jpeg',
                            small: 'surroundings/nature/Paklenica/' + tumbnailWidth + '/4.jpeg',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/5.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/6.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/7.jpeg',
                            small: 'surroundings/nature/Paklenica/' + tumbnailWidth + '/7.jpeg',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/8.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '2560',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/9.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Paklenica/' + fullscreenWidth + '/10.jpeg',
                            small: 'zoom.png',
                            title: 'Paklenica',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/nature/Paklenica/header.jpg'
                },
                {
                    name: $translate.instant('national_park_plitvice'),
                    distance: '170 km',
                    description: $translate.instant('national_park_plitvice_description'),
                    link: "http://www.np-plitvicka-jezera.hr/en/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/Plitvice+Lakes+National+Park,+Croatia/@44.388317,14.9996093,9z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x47615e6cfed90f3d:0x51a8ff8379a5e70!2m2!1d15.582012!2d44.865397?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-1.jpeg',
                            small: 'surroundings/nature/Plitvicka/' + tumbnailWidth + '/Plitvice-lakes-apartments-blanka-1.jpeg',
                            title: 'Big lake',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-2.jpeg',
                            small: 'surroundings/nature/Plitvicka/' + tumbnailWidth + '/Plitvice-lakes-apartments-blanka-2.jpeg',
                            title: 'Big lake',
                            width: '1920',
                            height: '1274',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-3.jpeg',
                            small: 'zoom.png',
                            title: 'Big lake',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-4.jpeg',
                            small: 'zoom.png',
                            title: 'Big lake',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-5.jpeg',
                            small: 'surroundings/nature/Plitvicka/' + tumbnailWidth + '/Plitvice-lakes-apartments-blanka-5.jpeg',
                            title: 'Big lake',
                            width: '1920',
                            height: '1382',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-6.jpeg',
                            small: 'zoom.png',
                            title: 'Big lake',
                            width: '1920',
                            height: '1279',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-7.jpeg',
                            small: 'zoom.png',
                            title: 'Big lake',
                            width: '1920',
                            height: '1271',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/nature/Plitvicka/' + fullscreenWidth + '/Plitvice-lakes-apartments-blanka-8.jpeg',
                            small: 'zoom.png',
                            title: 'Big lake',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }

                    ],
                    image: 'surroundings/nature/Plitvicka/header.jpg'
                }
            ];

            cities =[
                {
                    name: 'Pakoštane',
                    distance: '0 km',
                    description: $translate.instant('city_pakostane_description'),
                    link: "http://www.pakostane.hr/index.php?lang=en",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane/43.9063798,15.5081616/@43.909749,15.5039574,16z/data=!3m1!4b1!4m8!4m7!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m0?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/1.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/2.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/3.jpeg',
                            small: 'surroundings/cities/Pakostane/' + tumbnailWidth + '/3.jpeg',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1211',
                            show: 'true'
                        },
                        //{big:'surroundings/cities/Pakostane/'+fullscreenWidth+'/4.jpeg', small:'zoom.png', title:'Pakostane', width:'1920', height:'2880', show:'false'},
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/5.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1277',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/6.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '2836',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/7.jpeg',
                            small: 'surroundings/cities/Pakostane/' + tumbnailWidth + '/7.jpeg',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1277',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/8.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1277',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/9.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '2559',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/10.jpeg',
                            small: 'surroundings/cities/Pakostane/' + tumbnailWidth + '/10.jpeg',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1232',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/11.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/12.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Pakostane/' + fullscreenWidth + '/13.jpeg',
                            small: 'zoom.png',
                            title: 'Pakostane',
                            width: '1920',
                            height: '2880',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/cities/Pakostane/header.jpg'
                },
                {
                    name: "Biograd na moru",
                    distance: '5 km',
                    description: $translate.instant('city_biograd_description'),
                    link: "http://www.biograd-na-moru.com/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/Biograd+na+Moru,+Croatia/@43.9288192,15.4573865,14z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x1334cad63ad93431:0x7a890b4acf46390a!2m2!1d15.4435586!2d43.9373047?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/cities/Biograd/' + fullscreenWidth + '/1.jpeg',
                            small: 'zoom.png',
                            width: '1920',
                            height: '1080',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Biograd/' + fullscreenWidth + '/2.jpeg',
                            small: 'surroundings/cities/Biograd/' + tumbnailWidth + '/2.jpeg',
                            title: 'Biograd',
                            width: '1920',
                            height: '1285',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Biograd/' + fullscreenWidth + '/3.jpeg',
                            small: 'zoom.png',
                            title: 'Biograd',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Biograd/' + fullscreenWidth + '/4.jpeg',
                            small: 'surroundings/cities/Biograd/' + tumbnailWidth + '/4.jpeg',
                            title: 'Biograd',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Biograd/' + fullscreenWidth + '/5.jpeg',
                            small: 'surroundings/cities/Biograd/' + tumbnailWidth + '/5.jpeg',
                            title: 'Biograd',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }
                    ],
                    image: 'surroundings/cities/Biograd/header.jpg'
                },
                {
                    name: 'Zadar',
                    distance: '34 km',
                    description: $translate.instant('city_zadar_description'),
                    link: "http://www.zadar.hr/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/Zadar,+Croatia/@44.0216429,15.2959311,12z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x4761fa62d2c0b88f:0x12323e1c13f40784!2m2!1d15.2313648!2d44.119371?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-1.jpeg',
                            small: 'surroundings/cities/Zadar/' + tumbnailWidth + '/Zadar-apartments-blanka-1.jpeg',
                            title: 'Zadar',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-2.jpeg',
                            small: 'zoom.png',
                            title: 'Zadar',
                            width: '1920',
                            height: '1275',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-3.jpeg',
                            small: 'zoom.png',
                            title: 'Zadar',
                            width: '1920',
                            height: '779',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-4.jpeg',
                            small: 'zoom.png',
                            title: 'Zadar',
                            width: '1920',
                            height: '1409',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-5.jpeg',
                            small: 'zoom.png',
                            title: 'Zadar',
                            width: '1920',
                            height: '1440',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-6.jpeg',
                            small: 'zoom.png',
                            title: 'Zadar',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-8.jpeg',
                            small: 'zoom.png',
                            title: 'Zadar',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-9.jpeg',
                            small: 'surroundings/cities/Zadar/' + tumbnailWidth + '/Zadar-apartments-blanka-9.jpeg',
                            title: 'Zadar',
                            width: '1920',
                            height: '1274',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Zadar/' + fullscreenWidth + '/Zadar-apartments-blanka-10.jpeg',
                            small: 'surroundings/cities/Zadar/' + tumbnailWidth + '/Zadar-apartments-blanka-10.jpeg',
                            title: 'Zadar',
                            width: '1920',
                            height: '1280',
                            show: 'true'
                        }
                    ],
                    image: 'surroundings/cities/Zadar/header.jpg'
                },
                {
                    name: 'Šibenik',
                    distance: '42 km',
                    description: $translate.instant('city_sibenik_description'),
                    link: "http://www.sibenik-croatia.info/",
                    directions: 'https://www.google.ie/maps/dir/Apartments+Blanka,+Zadarska+ulica+13,+23211,+Pako%C5%A1tane,+Croatia/%C5%A0ibenik,+Croatia/@43.8679775,15.5765803,11z/data=!3m1!4b1!4m13!4m12!1m5!1m1!1s0x1334cc9bbef508e5:0x8830957a5773f290!2m2!1d15.508065!2d43.913116!1m5!1m1!1s0x13352615d088d09d:0x400ad50862bcc20!2m2!1d15.8952045!2d43.7350196?hl=en',
                    previewImages: [
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-1.jpeg',
                            small: 'surroundings/cities/Sibenik/' + tumbnailWidth + '/Sibenik-apartments-blanka-1.jpeg',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1440',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-2.jpeg',
                            small: 'surroundings/cities/Sibenik/' + tumbnailWidth + '/Sibenik-apartments-blanka-2.jpeg',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1285',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-3.jpeg',
                            small: 'zoom.png',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1278',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-4.jpeg',
                            small: 'zoom.png',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-5.jpeg',
                            small: 'surroundings/cities/Sibenik/' + tumbnailWidth + '/Sibenik-apartments-blanka-5.jpeg',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1444',
                            show: 'true'
                        },
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-6.jpeg',
                            small: 'zoom.png',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1289',
                            show: 'false'
                        },
                        {
                            big: 'surroundings/cities/Sibenik/' + fullscreenWidth + '/Sibenik-apartments-blanka-7.jpeg',
                            small: 'zoom.png',
                            title: 'Sibenik',
                            width: '1920',
                            height: '1280',
                            show: 'false'
                        }
                    ],
                    image: 'surroundings/cities/Sibenik/header.jpg'
                }];
            $rootScope.$broadcast('surroundings_translated', "");
        }

        translate();

        $rootScope.$on('$translateChangeSuccess', function (event, arg) {
            translate();
        });

        return {
            getBeaches: function () {
                return showBeaches();
            },

            getCities: function () {
                return showCities();
            }
            ,
            getNature: function () {
                return showNature();
            }
        };
    }
]);

