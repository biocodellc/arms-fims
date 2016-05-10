angular.module('fims.validation', ['fims.users', 'fims.modals', 'ui.bootstrap'])

    .controller('ValidationCtrl', ['$location', 'AuthFactory', 'PROJECT_ID', 'ExpeditionFactory', 'FailModalFactory', '$uibModal',
        function ($location, AuthFactory, PROJECT_ID, ExpeditionFactory, FailModalFactory, $uibModal) {
            // TODO fix the confusing variable names. armsProjects are biocode Expeditions
            var vm = this;
            vm.projectId = PROJECT_ID;
            vm.isAuthenticated = AuthFactory.isAuthenticated;
            vm.expeditionCode = "0";
            vm.armsProjects = [];
            vm.isPublicProject = false;
            vm.updateIsPublicProject = updateIsPublicProject();
            vm.createExpedition = createExpedition;

            function createExpedition() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'app/components/validation/createArmsProjectModal.html',
                    controller: 'CreateArmsProjectModalCtrl',
                    controllerAs: 'vm',
                    size: 'lg',
                    // resolve: {
                    //     items: function () {
                    //         return $scope.items;
                    //     }
                    // }
                });

                modalInstance.result.then(function (selectedItem) {
                    // pass in the projectCode to select the project
                    getArmsProjects();
                    $scope.selected = selectedItem;
                }, function () {
                });

            }

            function getArmsProjects(projectCode) {
                ExpeditionFactory.getExpeditions(vm.projectId)
                    .then(function(response) {
                        angular.extend(vm.armsProjects, response.data);
                        vm.expeditionCode = projectCode;
                    }, function (response, status) {
                        FailModalFactory.open("Failed to load expeditions", response.data.usrMessage);
                    })
            }
            
            function updateIsPublicProject() {
                if (vm.expeditionCode != 0) {
                    for (expedition in armsProjects) {
                        if (expedition.expeditionCode == vm.expeditionCode) {
                            vm.isPublicProject = expedition.public;
                            break;
                        }
                    }
                }
                
            }

            angular.element(document).ready(function() {
                fimsBrowserCheck($('#warning'));

                if (vm.isAuthenticated)
                    getArmsProjects();

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

.controller("CreateArmsProjectModalCtrl", ['$scope', '$uibModalInstance',
    function($scope, $uibModalInstance) {
        this.project = {
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
            $uibModalInstance.close($scope.project);
        };

        this.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);
