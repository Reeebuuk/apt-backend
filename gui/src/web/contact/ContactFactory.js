contactModule.factory('ContactFactory', ['DataService', '$log',
    function (DataService, $log) {
        function createSendContact(payload) {
            return DataService.executePostRequest('/api/contact', payload)
        }

        return {
            sendContact: function (payload) {
                return createSendContact(payload);
            }
        };
    }]);
