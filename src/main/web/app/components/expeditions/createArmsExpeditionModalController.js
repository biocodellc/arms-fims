angular.module('fims.expeditions')

.controller("CreateArmsExpeditionsModalCtrl", ['$scope', '$uibModalInstance', 'ExpeditionFactory', 'LoadingModalFactory',
    function($scope, $uibModalInstance, ExpeditionFactory, LoadingModalFactory) {
        var vm = this;
        vm.error = null;
        vm.expedition = {
            principalInvestigator: null,
            contactName: null,
            contactEmail: null,
            fundingSource: null,
            envisionedDuration: null,
            geographicScope: null,
            goals: null,
            leadOrganization: null,
            expeditionCode: null
        };

        vm.create = function () {
            $scope.$broadcast('show-errors-check-validity');

            if ($scope.expeditionForm.$invalid) { return; }

            LoadingModalFactory.open();
            ExpeditionFactory.createArmsExpedition(vm.expedition)
                .then(function() {
                    // handle success response
                    $uibModalInstance.close(vm.expedition.expeditionCode);
                }, function(response) {
                    if (response.data.usrMessage)
                        vm.error = response.data.usrMessage;
                    else
                        vm.error = "Server Error! Status code: " + response.status;
                })
                .finally(function() {
                    LoadingModalFactory.close();
                });
        };

        vm.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);