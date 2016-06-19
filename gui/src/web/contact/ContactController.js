contactModule.controller('ContactController', ['$scope', 'ContactFactory', '$q',
    function ($scope, ContactFactory, $q) {
        $scope.sending = false;
        $scope.success = false;
        $scope.error = false;

        $scope.hideAlert = function(){
            $scope.success = false;
            $scope.error = false;
            console.log("hide");
        };

        $scope.clearFields = function() {
            $scope.message = {
                name : "",
                email : "",
                subject : "",
                text : ""
            };
        };

        $scope.sendMessage = function(){
            $scope.error = false;
            $scope.success = false;
            if ($scope.message.email != "") {
                $scope.sending = true;
                var deferred = $q.defer();
                var promise = deferred.promise;
                deferred.resolve(ContactFactory.sendContact($scope.message));

                promise.then(function () {
                        $scope.sending = false;
                        $scope.success = true;
                        $scope.clearFields();
                    },
                    function () {
                        $scope.sending = false;
                        $scope.error = true;
                    });
            }
        };

        $scope.clearFields();
    }
]);