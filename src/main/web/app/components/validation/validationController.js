angular.module('fims.validation', ['fims.users', 'fims.modals'])

    .controller('ValidationCtrl', ['$rootScope', '$scope', '$location', 'AuthFactory', 'PROJECT_ID', 'ExpeditionFactory', 'FailModalFactory',
        function ($rootScope, $scope, $location, AuthFactory, PROJECT_ID, ExpeditionFactory, FailModalFactory) {
            // TODO fix the confusing variable names. armsProjects are biocode Expeditions
            var vm = this;
            vm.projectId = PROJECT_ID;
            vm.isAuthenticated = AuthFactory.isAuthenticated;
            vm.expeditionCode = "0";
            vm.armsProjects = [];
            vm.isPublicProject = false;
            vm.updateIsPublicProject = updateIsPublicProject();
            
            function getArmsProjects() {
                ExpeditionFactory.getExpeditions(vm.projectId)
                    .then(function(response) {
                        angular.extend(vm.armsProjects, response.data);
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

        }]);
