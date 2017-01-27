angular.module('fims.validation')

    .controller('ValidationCtrl', ['$scope', '$q', '$http', '$window', '$uibModal', 'Upload', 'AuthFactory', 'ExpeditionFactory', 'FailModalFactory', 'ResultsDataFactory', 'StatusPollingFactory', 'PROJECT_ID', 'REST_ROOT',
        function ($scope, $q, $http, $window, $uibModal, Upload, AuthFactory, ExpeditionFactory, FailModalFactory, ResultsDataFactory, StatusPollingFactory, PROJECT_ID, REST_ROOT) {
            // var latestExpeditionCode = null;
            var modalInstance = null;
            var vm = this;

            vm.projectId = PROJECT_ID;
            vm.isAuthenticated = AuthFactory.isAuthenticated;
            vm.newExpedition = false;
            vm.expeditonCode = null;
            vm.expeditions = [];
            vm.verifyDataPoints = false;
            vm.displayResults = false;
            vm.activeTab = 0;
            vm.fimsMetadataChange = fimsMetadataChange;
            vm.validate = validate;
            vm.upload = upload;
            vm.createExpedition = createExpedition;

            function createExpedition() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'app/components/expeditions/createArmsExpeditionModal.html',
                    controller: 'CreateArmsExpeditionsModalCtrl',
                    controllerAs: 'vm',
                    size: 'lg'
                });

                modalInstance.result.then(function (expeditionCode) {
                    getExpeditions();
                    if (expeditionCode) {
                        vm.expeditionCode = expeditionCode;
                    }
                }, function () {
                });

            }

            function upload() {
                $scope.$broadcast('show-errors-check-validity');

                if (vm.newExpedition) {
                    checkExpeditionExists()
                        .finally(function () {
                            if (vm.uploadForm.$invalid) {
                                return;
                            }

                            submitUpload();
                        });
                } else {
                    if (vm.uploadForm.$invalid) {
                        return;
                    }
                    submitUpload();
                }

            }

            function submitUpload() {
                var data = {
                    projectId: PROJECT_ID,
                    expeditionCode: vm.expeditionCode,
                    upload: true,
                    public: true,
                    fimsMetadata: vm.fimsMetadata
                };

                validateSubmit(data).then(
                    function (response) {
                        if (response.data.done) {
                            ResultsDataFactory.validationMessages = response.data.done;
                            ResultsDataFactory.showOkButton = true;
                            ResultsDataFactory.showValidationMessages = true;
                        } else if (response.data.continue) {
                            if (response.data.continue.message == "continue") {
                                continueUpload();
                            } else {
                                ResultsDataFactory.validationMessages = response.data.continue;
                                ResultsDataFactory.showValidationMessages = true;
                                ResultsDataFactory.showStatus = false;
                                ResultsDataFactory.showContinueButton = true;
                                ResultsDataFactory.showCancelButton = true;
                            }
                        } else {
                            ResultsDataFactory.error = "Unexpected response from server. Please contact system admin.";
                            ResultsDataFactory.showOkButton = true;
                        }
                    });
            }

            $scope.$on("resultsModalContinueUploadEvent", function () {
                continueUpload();
                ResultsDataFactory.showContinueButton = false;
                ResultsDataFactory.showCancelButton = false;
                ResultsDataFactory.showValidationMessages = false;
            });

            function continueUpload() {
                StatusPollingFactory.startPolling();
                return $http.get(REST_ROOT + "validate/continue?createExpedition=" + vm.newExpedition).then(
                    function (response) {
                        if (response.data.error) {
                            ResultsDataFactory.error = response.data.error;
                        } else if (response.data.continue) {
                            ResultsDataFactory.uploadMessage = response.data.continue.message;
                            ResultsDataFactory.showOkButton = false;
                            ResultsDataFactory.showContinueButton = true;
                            ResultsDataFactory.showCancelButton = true;
                            ResultsDataFactory.showStatus = false;
                            ResultsDataFactory.showUploadMessages = true;
                        } else {
                            ResultsDataFactory.successMessage = response.data.done;
                            modalInstance.close();
                            resetForm();
                        }

                    }, function (response) {
                        ResultsDataFactory.reset();
                        ResultsDataFactory.error = response.data.error || response.data.usrMessage || "Server Error!";
                        ResultsDataFactory.showOkButton = true;
                    })
                    .finally(
                        function () {
                            if (vm.newExpedition) {
                                getExpeditions();
                            }
                            StatusPollingFactory.stopPolling();
                        }
                    );
            }

            function validateSubmit(data) {
                ResultsDataFactory.reset();
                // start polling here, since firefox support for progress events doesn't seem to be very good
                StatusPollingFactory.startPolling();
                openResultsModal();
                return Upload.upload({
                    url: REST_ROOT + "validate",
                    data: data
                }).then(
                    function (response) {
                        return response;
                    },
                    function (response) {
                        ResultsDataFactory.error = response.data.usrMessage || "Server Error!";
                        ResultsDataFactory.showOkButton = true;
                        return response;
                    }
                ).finally(function () {
                    StatusPollingFactory.stopPolling();
                });

            }

            function validate() {
                validateSubmit({
                    projectId: PROJECT_ID,
                    expeditionCode: vm.expeditionCode,
                    upload: false,
                    fimsMetadata: vm.fimsMetadata
                }).then(
                    function (response) {
                        ResultsDataFactory.validationMessages = response.data.done;
                        modalInstance.close();
                    }
                );
            }

            function openResultsModal() {
                modalInstance = $uibModal.open({
                    templateUrl: 'app/components/validation/results/resultsModal.tpl.html',
                    size: 'md',
                    controller: 'ResultsModalCtrl',
                    controllerAs: 'vm',
                    windowClass: 'app-modal-window',
                    backdrop: 'static'
                });

                modalInstance.result
                    .finally(function () {
                            vm.displayResults = true;
                            if (!ResultsDataFactory.error) {
                                vm.activeTab = 2; // index 2 is the results tab
                            }
                            ResultsDataFactory.showStatus = false;
                            ResultsDataFactory.showValidationMessages = true;
                            ResultsDataFactory.showSuccessMessages = true;
                            ResultsDataFactory.showUploadMessages = false;
                        }
                    )

            }

            function resetForm() {
                vm.fimsMetadata = null;
                vm.expeditionCode = null;
                $scope.$broadcast('show-errors-reset');
            }

            function fimsMetadataChange() {
                // Clear the results
                ResultsDataFactory.reset();

                // Check NAAN
                parseSpreadsheet("~naan=[0-9]+~", "Instructions").then(
                    function (spreadsheetNaan) {
                        if (spreadsheetNaan > 0) {
                            $http.get(REST_ROOT + "utils/getNAAN")
                                .then(function (response) {
                                    checkNAAN(spreadsheetNaan, response.data.naan);
                                });
                        }
                    });

                generateMap('map', PROJECT_ID, vm.fimsMetadata).then(
                    function () {
                        vm.verifyDataPoints = true;
                    }, function () {
                        vm.verifyDataPoints = false;
                    }).always(function () {
                    // this is a hack since we are using jQuery for generateMap
                    $scope.$apply();
                });
            }

            function parseSpreadsheet(regExpression, sheetName) {
                try {
                    f = new FileReader();
                } catch (err) {
                    return null;
                }
                var deferred = new $q.defer();
                // older browsers don't have a FileReader
                if (f != null) {

                    var splitFileName = vm.fimsMetadata.name.split('.');
                    if (XLSXReader.exts.indexOf(splitFileName[splitFileName.length - 1]) > -1) {
                        $q.when(XLSXReader.utils.findCell(vm.fimsMetadata, regExpression, sheetName)).then(function (match) {
                            if (match) {
                                deferred.resolve(match.toString().split('=')[1].slice(0, -1));
                            } else {
                                deferred.resolve(null);
                            }
                        });
                        return deferred.promise;
                    }
                }
                setTimeout(function () {
                    deferred.resolve(null)
                }, 100);
                return deferred.promise;

            }

            // function to verify naan's
            function checkNAAN(spreadsheetNaan, naan) {
                if (spreadsheetNaan != naan) {
                    var buttons = {
                        "Ok": function () {
                            $("#dialogContainer").removeClass("error");
                            $(this).dialog("close");
                        }
                    };
                    var message = "Spreadsheet appears to have been created using a different FIMS/BCID system.<br>";
                    message += "Spreadsheet says NAAN = " + spreadsheetNaan + "<br>";
                    message += "System says NAAN = " + naan + "<br>";
                    message += "Proceed only if you are SURE that this spreadsheet is being called.<br>";
                    message += "Otherwise, re-load the proper FIMS system or re-generate your spreadsheet template.";

                    dialog(message, "NAAN check", buttons);
                }
            }

            function checkExpeditionExists() {
                return ExpeditionFactory.getExpedition(vm.expeditionCode)
                    .then(function (response) {
                        // if we get an expedition, then it already exists
                        if (response.data) {
                            vm.uploadForm.newExpeditionCode.$setValidity("exists", false);
                        } else {
                            vm.uploadForm.newExpeditionCode.$setValidity("exists", true);
                        }
                    });
            }

            function getExpeditions() {
                ExpeditionFactory.getArmsExpeditions(true)
                    .then(function (response) {
                        angular.extend(vm.expeditions, response.data);
                    }, function (response, status) {
                        FailModalFactory.open("Failed to load projects", response.data.usrMessage);
                    })
            }

            (function init() {
                fimsBrowserCheck($('#warning'));

                if (vm.isAuthenticated) {
                    getExpeditions();
                }
            }).call();

        }]);
