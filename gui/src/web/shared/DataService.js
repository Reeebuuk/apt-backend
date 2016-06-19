sharedModule.factory('DataService', ['$http', '$q', '$log', function ($http, $q, $log)
{
    function makeGetRequest(url, id)
    {
        var request = {
            method : 'GET',
            url: url + "/" + id
        };

        var dfd = $q.defer();
        $http(request).then(
                function (responseSuccessData) {
                    dfd.resolve(responseSuccessData.data);
                },
                function (responseErrorData) {
                    dfd.reject(responseErrorData.data);
                }
        );
        return dfd.promise;
    }

    function makePagableGetRequest(url, filters) {
        var request = {
            method: 'GET',
            url: url + appendFilter(filters)
        };

        var dfd = $q.defer();
        $http(request).then(
            function (responseSuccessData) {
                dfd.resolve(responseSuccessData.data);
            },
            function (responseErrorData) {
                dfd.reject(responseErrorData.data);
            }
        );
        return dfd.promise;
    }

    function appendFilter(filters)
    {
        if (filters === null)
        {
            return "";
        }
        var filterString = "?";
        for (var filter in filters)
        {
            if (filters.hasOwnProperty(filter) && filters[filter] !== "")
            {
                if (filterString.length > 1)
                {
                    filterString += "&";
                }

                filterString += filter + "=" + filters[filter];
            }
        }
        return filterString;
    }

    function makePostRequest(url, payload)
    {
        var request = {
            method : 'POST',
            url : url,
            data : payload
        };

        var dfd = $q.defer();
        $http(request).then(
                function (responseSuccessData) {
                    dfd.resolve(responseSuccessData.data);
                },
                function (responseErrorData) {
                    dfd.reject(responseErrorData.data);
                }
        );
        return dfd.promise;
    }

    function makePutRequest(url, id, payload)
    {
        var request = {
            method : 'PUT',
            url : url + "/" + id,
            data : payload
        };

        var dfd = $q.defer();
        $http(request).then(
                function (responseSuccessData) {
                    dfd.resolve(responseSuccessData.data);
                },
                function (responseErrorData) {
                    dfd.reject(responseErrorData.data);
                }
        );
        return dfd.promise;
    }

    function makeDeleteRequest(url, id)
    {
        var request = {
            method : 'DELETE',
            url : url + "/" + id
        };

        var dfd = $q.defer();
        $http(request).then(
                function (responseSuccessData) {
                    dfd.resolve(responseSuccessData.data);
                },
                function (responseErrorData) {
                    dfd.reject(responseErrorData.data);
                }
        );
        return dfd.promise;
    }

    return {
        executePagableGetRequest: function (url, filters) {
            return makePagableGetRequest(url, filters);
        },
        executeGetRequestWithoutParams: function (url) {
            return makePagableGetRequest(url, null);
        },
        executeGetRequest: function (url, id) {
            return makeGetRequest(url, id);
        },
        executePostRequest: function (url, payload) {
            return makePostRequest(url, payload);
        },
        executePutRequest: function (url, id, payload) {
            return makePutRequest(url, id, payload);
        },
        executeDeleteRequest: function (url, id) {
            return makeDeleteRequest(url, id);
        }
    };
}]);

