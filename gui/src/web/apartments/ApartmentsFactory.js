apartmentsModule.factory('ApartmentsFactory', ['PictureSizeFactory', 'DataService', '$translate', '$rootScope',
    function (PictureSizeFactory, DataService, $translate, $rootScope)
    {
        var fullscreenWidth = PictureSizeFactory.getFullscreenWidth();
        var tumbnailWidth = PictureSizeFactory.getTumbnailWidth();
        var kruno_app, blanka_app, djuro_app;

        function translate() {
            kruno_app =
            {
                apartmentId: 1,
                title: $translate.instant('apartment_kruno'),
                subtitle: $translate.instant('all_about_style'),
                bedroomNo: 1,
                bedsNo: "2+1",
                questsNo: 3,
                previewImages: [
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/8.jpg',
                        small: 'apartments/kruno/' + tumbnailWidth + '/8.jpg',
                        title: 'Balcony',
                        width: '5458',
                        height: '3155',
                        show: 'true'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/1.jpg',
                        small: 'zoom.png',
                        title: 'Bathroom',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/2.jpg',
                        small: 'apartments/kruno/' + tumbnailWidth + '/2.jpg',
                        title: 'Living room',
                        width: '5520',
                        height: '3353',
                        show: 'true'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/3.jpg',
                        small: 'zoom.png',
                        title: 'Living room',
                        width: '5095',
                        height: '2945',
                        show: 'false'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/4.jpg',
                        small: 'zoom.png',
                        title: 'Living room',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/5.jpg',
                        small: 'zoom.png',
                        title: 'Living room',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/6.jpg',
                        small: 'apartments/kruno/' + tumbnailWidth + '/6.jpg',
                        title: 'Kitchen',
                        width: '5458',
                        height: '3155',
                        show: 'true'
                    },
                    {
                        big: 'apartments/kruno/' + fullscreenWidth + '/7.jpg',
                        small: 'zoom.png',
                        title: 'Kitchen',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/12.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    //{big:'apartments/house/'+fullscreenWidth+'/27.jpg', small:'zoom.png', title:'House from the front', width:'5458', height:'3155', show:'false'},
                    //{big:'apartments/house/'+fullscreenWidth+'/28.jpg', small:'zoom.png', title:'House from the front', width:'2592', height:'1936', show:'false'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/29.jpeg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '2592',
                        height: '1936',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/30.jpeg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '2592',
                        height: '1936',
                        show: 'false'
                    },
                    //{big:'apartments/house/'+fullscreenWidth+'/22.jpg', small:'zoom.png', title:'House from the front', width:'5458', height:'3155', show:'false'},
                    //{big:'apartments/house/'+fullscreenWidth+'/23.jpg', small:'zoom.png', title:'House from the street', width:'5458', height:'3155', show:'false'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/24.jpg',
                        small: 'zoom.png',
                        title: 'House from the street',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/25.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/26.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    }
                ],
                image: 'apartments/kruno/header-kruno.jpg',
                price: 0
            };
            blanka_app =
            {
                apartmentId: 2,
                title: $translate.instant('apartment_blanka'),
                subtitle: $translate.instant('biggest_apartment'),
                bedroomNo: 1,
                bedsNo: "2+2",
                questsNo: 4,
                previewImages: [
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/10.jpg',
                        small: 'apartments/blanka/' + tumbnailWidth + '/10.jpg',
                        title: 'Balcony',
                        width: '5520',
                        height: '3680',
                        show: 'true'
                    },
                    //{big:'apartments/blanka/'+fullscreenWidth+'/11.jpg', small:'apartments/blanka/'+tumbnailWidth+'/11.jpg', title:'Balcony', width:'5520', height:'3680', show:'false'},
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/01.jpg',
                        small: 'zoom.png',
                        title: 'Living room - Sofa is replaced',
                        width: '5140',
                        height: '2971',
                        show: 'false'
                    },
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/13.jpg',
                        small: 'zoom.png',
                        title: 'Living room - new sofa',
                        width: '5140',
                        height: '2971',
                        show: 'false'
                    },
                    //{big:'apartments/blanka/'+fullscreenWidth+'/02.jpg', small:'apartments/blanka/'+tumbnailWidth+'/02.jpg', title:'Living room', width:'5246', height:'3032', show:'true'},
                    //{big:'apartments/blanka/'+fullscreenWidth+'/03.jpg', small:'zoom.png', title:'Living room', width:'5458', height:'3155', show:'false'},
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/04.jpg',
                        small: 'zoom.png',
                        title: 'Kitchen',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/05.jpg',
                        small: 'apartments/blanka/' + tumbnailWidth + '/05.jpg',
                        title: 'Bathroom',
                        width: '5458',
                        height: '3155',
                        show: 'true'
                    },
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/06.jpg',
                        small: 'zoom.png',
                        title: 'Bathtub',
                        width: '3535',
                        height: '4681',
                        show: 'false'
                    },
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/07.jpg',
                        small: 'apartments/blanka/' + tumbnailWidth + '/07.jpg',
                        title: 'Bedroom',
                        width: '5458',
                        height: '3155',
                        show: 'true'
                    },
                    {
                        big: 'apartments/blanka/' + fullscreenWidth + '/08.jpg',
                        small: 'zoom.png',
                        title: 'Bedroom',
                        width: '5520',
                        height: '3496',
                        show: 'false'
                    },
                    //{big:'apartments/blanka/'+fullscreenWidth+'/09.jpg', small:'apartments/blanka/'+tumbnailWidth+'/09.jpg', title:'Balcony', width:'5520', height:'3680', show:'true'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/12.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    //{big:'apartments/house/'+fullscreenWidth+'/27.jpg', small:'zoom.png', title:'House from the front', width:'5458', height:'3155', show:'false'},
                    //{big:'apartments/house/'+fullscreenWidth+'/28.jpg', small:'zoom.png', title:'House from the front', width:'2592', height:'1936', show:'false'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/29.jpeg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '2592',
                        height: '1936',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/30.jpeg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '2592',
                        height: '1936',
                        show: 'false'
                    },
                    //{big:'apartments/house/'+fullscreenWidth+'/22.jpg', small:'zoom.png', title:'House from the front', width:'5458', height:'3155', show:'false'},
                    //{big:'apartments/house/'+fullscreenWidth+'/23.jpg', small:'zoom.png', title:'House from the street', width:'5458', height:'3155', show:'false'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/24.jpg',
                        small: 'zoom.png',
                        title: 'House from the street',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/25.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/26.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    }
                ],
                image: 'apartments/blanka/header-blanka.jpg',
                price: 0
            };
            djuro_app =
            {
                apartmentId: 3,
                title: $translate.instant('apartment_djuro'),
                subtitle: $translate.instant('spacious_apartment'),
                bedroomNo: 1,
                bedsNo: "2+2",
                questsNo: 4,
                previewImages: [
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/10.jpg',
                        small: 'apartments/djuro/' + tumbnailWidth + '/10.jpg',
                        title: 'Balcony',
                        width: '5233',
                        height: '3025',
                        show: 'true'
                    },
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/01.jpg',
                        small: 'apartments/djuro/' + tumbnailWidth + '/01.jpg',
                        title: 'Bedroom',
                        width: '5140',
                        height: '2971',
                        show: 'true'
                    },
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/02.jpg',
                        small: 'zoom.png',
                        title: 'Bedroom',
                        width: '5246',
                        height: '3032',
                        show: 'false'
                    },
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/05.jpg',
                        small: 'zoom.png',
                        title: 'Bedroom',
                        width: '5184',
                        height: '2996',
                        show: 'false'
                    },
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/03.jpg',
                        small: 'zoom.png',
                        title: 'Bathroom',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    //{big:'apartments/djuro/'+fullscreenWidth+'/04.jpg', small:'apartments/djuro/'+tumbnailWidth+'/04.jpg', title:'Bathroom', width:'3680', height:'5063', show:'false'},
                    //{big:'apartments/djuro/'+fullscreenWidth+'/06.jpg', small:'apartments/djuro/'+tumbnailWidth+'/06.jpg', title:'Living room', width:'5520', height:'3680', show:'false'},
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/07.jpg',
                        small: 'apartments/djuro/' + tumbnailWidth + '/07.jpg',
                        title: 'Living room',
                        width: '5458',
                        height: '3155',
                        show: 'true'
                    },
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/08.jpg',
                        small: 'zoom.png',
                        title: 'Living room',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/djuro/' + fullscreenWidth + '/09.jpg',
                        small: 'zoom.png',
                        title: 'Kitchen',
                        width: '5458',
                        height: '5458',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/12.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    //{big:'apartments/house/'+fullscreenWidth+'/27.jpg', small:'zoom.png', title:'House from the front', width:'5458', height:'3155', show:'false'},
                    //{big:'apartments/house/'+fullscreenWidth+'/28.jpg', small:'zoom.png', title:'House from the front', width:'2592', height:'1936', show:'false'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/29.jpeg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '2592',
                        height: '1936',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/30.jpeg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '2592',
                        height: '1936',
                        show: 'false'
                    },
                    //{big:'apartments/house/'+fullscreenWidth+'/22.jpg', small:'zoom.png', title:'House from the front', width:'5458', height:'3155', show:'false'},
                    //{big:'apartments/house/'+fullscreenWidth+'/23.jpg', small:'zoom.png', title:'House from the street', width:'5458', height:'3155', show:'false'},
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/24.jpg',
                        small: 'zoom.png',
                        title: 'House from the street',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/25.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    },
                    {
                        big: 'apartments/house/' + fullscreenWidth + '/26.jpg',
                        small: 'zoom.png',
                        title: 'House from the front',
                        width: '5458',
                        height: '3155',
                        show: 'false'
                    }
                ],
                image: 'apartments/djuro/header-djuro.jpg',
                price: 0
            };
            $rootScope.$broadcast('apartments_translated', "");
        }

        translate();

        $rootScope.$on('$translateChangeSuccess', function (event, arg) {
            translate();
        });

        function getAvailableApartmentsArray(takenApartmentsIds){
            var apartments = [];
            if ($.inArray('2', takenApartmentsIds) > -1){
                apartments.push(blanka_app)
            }
            if ($.inArray('1', takenApartmentsIds) > -1){
                apartments.push(kruno_app)
            }
            if ($.inArray('3', takenApartmentsIds) > -1){
                apartments.push(djuro_app)
            }
            return apartments;
        }

        function showAllApartments() {
            return [kruno_app, blanka_app, djuro_app]
        }

        function createGetAvailableApartments(from, to)
        {
            var filters = {};
            filters["from"] = from.getTime();
            filters["to"] = to.getTime();

            return DataService.executePagableGetRequest('/api/booking/available', filters)
        }

        return {
            getAvailableApartments: function (takenApartmentsIds){
                return getAvailableApartmentsArray(takenApartmentsIds);
            },

            getAllApartments: function() {
                return showAllApartments();
            },

            getAvailableApartmentsForRange: function (from, to)
            {
                return createGetAvailableApartments(from, to);
            }
        };
    }]);

