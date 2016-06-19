sharedModule.animation(".show160", function () {
    return {
        addClass: function (element, className) {
            TweenMax.to(element, 1, {height:160, width:160, opacity:1});
        }
    }
});

sharedModule.animation(".show50", function () {
    return {
        addClass: function (element, className) {
            TweenMax.to(element, 1, {height:50, width:50, opacity:1});
        }
    }
});

sharedModule.animation(".appear", function () {
    return {
        addClass: function (element, className) {
            TweenMax.to(element, 2, {opacity:1});
        },
        removeClass: function (element, className) {
            TweenMax.to(element, 1, {opacity:0});
        }
    }
});

sharedModule.animation(".slideIt", function () {
    return {
        addClass: function (element, className) {
            TweenMax.to(element, 1, {left:0, opacity:1});
        }
    }
});