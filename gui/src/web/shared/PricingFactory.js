sharedModule.factory('PricingFactory', ['DataService',

    function (DataService)
    {
        function createGetPricingList(){
            return DataService.executeGetRequestWithoutParams('/api/pricing')
        }

        function fetchPriceForRange(id, from, to)
        {
            from.setHours(12);
            from.setMinutes(0);
            from.setSeconds(0);
            from.setMilliseconds(0);

            to.setHours(12);
            to.setMinutes(0);
            to.setSeconds(0);
            to.setMilliseconds(0);
            var filters = {};
            filters["from"] = from.getTime();
            filters["to"] = to.getTime();
            filters["apartmentId"] = id;

            return DataService.executePagableGetRequest('/api/pricing/range', filters)
        }

        return {
            getPricingList: function() {
                return createGetPricingList();
            },

            getPriceForRange: function(id, from, to) {
                return fetchPriceForRange(id, from, to);
            }
        };
    }]);

