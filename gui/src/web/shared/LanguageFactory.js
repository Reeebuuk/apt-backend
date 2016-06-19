sharedModule.factory('LanguageFactory', ['$translate', '$locale', '$rootScope', '$cookies', function ($translate, $locale, $rootScope, $cookies) {

    var currentLanguage;

    var languages = [
        {
            name: "English",
            class: "en"
        },
        {
            name: "Hrvatski",
            class: "hr"
        }
    ];

    function setInitialLanguage() {
        var lang_class = $cookies.get('lang_class');
        var lang_name = $cookies.get('lang_name');
        if (lang_class == null || lang_name == null) {
            currentLanguage = languages[0];
        }
        else {
            currentLanguage = {
                name: lang_name,
                class: lang_class
            };
            changeLanguage(currentLanguage)
        }
    }

    setInitialLanguage();

    function translateLocale() {
        var locale = {
            "DATETIME_FORMATS": {
                "DAY": [
                    $translate.instant("sunday"),
                    $translate.instant("monday"),
                    $translate.instant("tuesday"),
                    $translate.instant("wednesday"),
                    $translate.instant("thursday"),
                    $translate.instant("friday"),
                    $translate.instant("saturday")
                ],
                "SHORTDAY": [
                    $translate.instant("sun"),
                    $translate.instant("mon"),
                    $translate.instant("tue"),
                    $translate.instant("wed"),
                    $translate.instant("thu"),
                    $translate.instant("fri"),
                    $translate.instant("sat")
                ],
                "MONTH": [
                    $translate.instant("january"),
                    $translate.instant("february"),
                    $translate.instant("march"),
                    $translate.instant("april"),
                    $translate.instant("may"),
                    $translate.instant("june"),
                    $translate.instant("july"),
                    $translate.instant("august"),
                    $translate.instant("september"),
                    $translate.instant("october"),
                    $translate.instant("november"),
                    $translate.instant("december")
                ],
                "SHORTMONTH": [
                    $translate.instant("jan"),
                    $translate.instant("feb"),
                    $translate.instant("mar"),
                    $translate.instant("apr"),
                    $translate.instant("may_short"),
                    $translate.instant("jun"),
                    $translate.instant("jul"),
                    $translate.instant("aug"),
                    $translate.instant("sep"),
                    $translate.instant("oct"),
                    $translate.instant("nov"),
                    $translate.instant("dec")
                ]
            }
        };

        angular.copy(locale, $locale);
    }

    function changeLanguage(language){
        $cookies.put('lang_class', language.class, 5);
        $cookies.put('lang_name', language.name, 5);
        currentLanguage = language;
        $translate.use(language.class);
    }

    $rootScope.$on('$translateChangeSuccess', function (event, arg) {
        translateLocale();
    });

    return {
        setCurrentLanguage: function (language) {
            changeLanguage(language);
        },
        getCurrentLanguage: function() {
            return currentLanguage;
        },
        getAllLanguages: function() {
            return languages;
        }
    };
}]);

