angular.module('fims.validation', ['fims.users', 'fims.modals', 'ui.bootstrap'])

    .controller('ValidationCtrl', ['$location', 'AuthFactory', 'PROJECT_ID', 'ExpeditionFactory', 'FailModalFactory', '$uibModal',
        function ($location, AuthFactory, PROJECT_ID, ExpeditionFactory, FailModalFactory, $uibModal) {
            var vm = this;
            vm.projectId = PROJECT_ID;
            vm.isAuthenticated = AuthFactory.isAuthenticated;
            vm.expeditionCode = "0";
            vm.armsExpeditions = [];
            vm.isPublicExpedition = false;
            vm.updateIsPublicExpedition = updateIsPublicExpedition();
            vm.createExpedition = createExpedition;

            function createExpedition() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'app/components/validation/createArmsExpeditionModal.html',
                    controller: 'CreateArmsExpeditionsModalCtrl',
                    controllerAs: 'vm',
                    size: 'lg',
                    // resolve: {
                    //     items: function () {
                    //         return $scope.items;
                    //     }
                    // }
                });

                modalInstance.result.then(function (selectedItem) {
                    // pass in the expeditionCode to select the expedition 
                    getArmsExpeditions();
                    $scope.selected = selectedItem;
                }, function () {
                });

            }

            function getArmsExpeditions(expeditionCode) {
                ExpeditionFactory.getExpeditions(vm.projectId)
                    .then(function(response) {
                        angular.extend(vm.armsExpeditions, response.data);
                        vm.expeditionCode = expeditionCode;
                    }, function (response, status) {
                        FailModalFactory.open("Failed to load ARMS projects", response.data.usrMessage);
                    })
            }
            
            function updateIsPublicExpedition() {
                if (vm.expeditionCode != 0) {
                    for (expedition in armsExpeditions) {
                        if (expedition.expeditionCode == vm.expeditionCode) {
                            vm.isPublicExpedition = expedition.public;
                            break;
                        }
                    }
                }
                
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
                jQuery("body").on("click", "#groupMessage", function () {
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

.controller("CreateArmsExpeditionsModalCtrl", ['$scope', '$uibModalInstance',
    function($scope, $uibModalInstance) {
        this.expedition = {
            principalInvestigator: null,
            contactName: null,
            contactEmail: null,
            fundingSource: null,
            envisionedDuration: null,
            geographicScope: null,
            goals: null,
            leadOrganization: null
        };

        this.create = function (form) {
            $uibModalInstance.close($scope.expedition);
        };

        this.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);
