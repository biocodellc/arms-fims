angular.module('fims.validation', ['fims.users', 'fims.modals', 'ui.bootstrap', 'fims.expeditions'])

    .controller('ValidationCtrl', ['$location', 'AuthFactory', 'PROJECT_ID', 'ExpeditionFactory', 'FailModalFactory', '$uibModal',
        function ($location, AuthFactory, PROJECT_ID, ExpeditionFactory, FailModalFactory, $uibModal) {
            var vm = this;
            vm.projectId = PROJECT_ID;
            vm.isAuthenticated = AuthFactory.isAuthenticated;
            vm.expeditionCode = "0";
            vm.armsExpeditions = [];
            vm.createExpedition = createExpedition;

            function createExpedition() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'app/components/validation/createArmsExpeditionModal.html',
                    controller: 'CreateArmsExpeditionsModalCtrl',
                    controllerAs: 'vm',
                    size: 'lg',
                });

                modalInstance.result.then(function (expeditionCode) {
                    getArmsExpeditions();
                    vm.expeditionCode = expeditionCode;
                }, function () {
                });

            }

            function getArmsExpeditions(expeditionCode) {
                ExpeditionFactory.getArmsExpeditions(true)
                    .then(function(response) {
                        angular.extend(vm.armsExpeditions, response.data);
                        if (expeditionCode)
                            vm.expeditionCode = expeditionCode;
                    }, function (response, status) {
                        FailModalFactory.open("Failed to load ARMS projects", response.data.usrMessage);
                    })
            }
            

            angular.element(document).ready(function() {
                fimsBrowserCheck($('#warning'));

                if (vm.isAuthenticated)
                    getArmsExpeditions();

                validationFormToggle();

                // call validatorSubmit if the enter key was pressed in an input
                $("input").keydown( function(event) {
                    if (event.which == 13) {
                        event.preventDefault();
                        validatorSubmit();
                    }
                });

                $("#validationSubmit").click(function() {
                    validatorSubmit();
                });

                // expand/contract messages -- use 'on' function and initially to 'body' since this is dynamically loaded
                jQuery("body").off().on("click", "#groupMessage", function () {
                    $(this).parent().siblings("dd").slideToggle();
                });

                if ($location.search()['error']) {
                    $("#dialogContainer").addClass("error");
                    dialog("Authentication Error!<br><br>" + $location.search()['error'] + "Error", {"OK": function() {
                        $("#dialogContainer").removeClass("error");
                        $(this).dialog("close"); }
                    });
                }
            });

        }])

.controller("CreateArmsExpeditionsModalCtrl", ['$scope', '$uibModalInstance', 'ExpeditionFactory',
    function($scope, $uibModalInstance, ExpeditionFactory) {
        var vm = this;
        vm.error;
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

            ExpeditionFactory.createArmsExpedition(vm.expedition)
                .then(function() {
                    // handle success response
                    $uibModalInstance.close(vm.expedition.expeditionCode);
                }, function(response) {
                    if (response.data.usrMessage)
                        vm.error = response.data.usrMessage;
                    else
                        vm.error = "Server Error! Status code: " + response.status;
                });
        };

        vm.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);
