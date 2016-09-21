var app = angular.module('armsSearchApp', []);

app.controller('searchCtrl', ['$http', '$filter', '$window',
    function ($http, $filter, $window) {
        var defaultFilter = {
            attributeIndex: "0",
            operator: "",
            value: "",
        };

        var operatorMap = {
            "EQUALS": "=",
            "LESS_THEN": "<",
            "GREATER_THEN": ">",
            "ENDS_WITH": "Ends With",
            "STARTS_WITH": "Starts With",
            "CONTAINS": "Contains",
            "IN": "In"
        };

        var displayOperators = {};

        var vm = this;
        vm.error = null;
        vm.expeditionId = null;
        vm.restUri = null;
        vm.error = null;
        vm.filterOptions = {};
        vm.armsExpeditions = [];
        vm.deployments = [];
        vm.deploymentIds = ["all"];
        vm.filters = [];
        vm.queryResults = undefined;
        vm.updateDeployments = updateDeployments;
        vm.removeFilter = removeFilter;
        vm.addFilter = addFilter;
        vm.getOperators = getOperators;
        vm.query = query;
        vm.excel = excel;
        vm.map = map;
        vm.formatDateTime = formatDateTime;

        function map() {
            var criterions = JSON.stringify({"criterion": getFilterCriterion()})
            $http.post("/deployments/search/map", criterions).then(
                function (response) {
                    if (response.data.url) {
                        $window.open(response.data.url);
                    } else {
                        vm.error = "Error fetching query information for map";
                    }
                }, function (response) {
                    vm.error = "Error fetching query information for map";
                });
        }

        function excel() {
            var criterions = JSON.stringify({"criterion": getFilterCriterion()})
            $http.post("/deployments/search/excel", criterions).then(
                function (response) {
                    if (response.data.url) {
                        $window.open(response.data.url);
                    } else {
                        vm.error = "Error downloading query";
                    }
                }, function (response) {
                    vm.error = "Error downloading query";
                });
        }

        function formatDateTime(column, dateTime) {
            var value = "N/A";

            if (dateTime) {
                angular.forEach(vm.filterOptions.attributes, function (attribute) {
                    if (attribute.column == column) {
                        var format = attribute.dataformat;
                        value = $filter('date')(dateTime, format);
                    }
                });
            }

            return value;
        }

        function query() {
            var criterions = JSON.stringify({"criterion": getFilterCriterion()})
            $http.post(vm.restUri + "deployments/query/json", criterions).then(
                function (response) {
                    vm.queryResults = response.data;
                    jQuery.bootstrapSortable();
                }, function (response) {
                    vm.error = "Error fetching query results.";
                });
        }

        function getFilterCriterion() {
            var filters = [];
            angular.forEach(vm.filters, function (filter, index) {
                if (filter.value) {
                    var criteria = {
                        "key": vm.filterOptions.attributes[filter.attributeIndex].column,
                        "operator": filter.operator,
                        "value": filter.value,
                        "condition": "AND"
                    };
                    filters.push(criteria);
                }
            });

            var deploymentIds = [];
            if (vm.deploymentIds.indexOf("all") > -1) {
                angular.forEach(vm.deployments, function (deployment) {
                    deploymentIds.push(deployment.deploymentId);
                });
            } else {
                deploymentIds = vm.deploymentIds;
            }

            if (deploymentIds.length > 0) {
                var criteria = {
                    "key": "deploymentId",
                    "operator": "IN",
                    "value": deploymentIds.join(),
                    "condition": "AND"
                };
                filters.push(criteria);
            }

            return filters;
        }

        function getOperators(attributeIndex) {
            var dataType = vm.filterOptions.attributes[attributeIndex].datatype;
            return displayOperators[dataType];
        }

        // We need to map the operators we fetched from fims into a map with the operators we want to display.
        // ex. Fims Data is "Equals" we map that to =. operatorMap object holds our mapping.
        // We can't do this in getOperators function due to angular databinding and infinite loop
        function transformOperators() {
            angular.forEach(vm.filterOptions.operators, function (operators, dataType) {
                var operatorsForDataType = {};
                angular.forEach(operators, function (operator, index) {
                    operatorsForDataType[operator] = (operatorMap[operator] ? operatorMap[operator] : operator);
                });
                displayOperators[dataType] = operatorsForDataType;

            });
        }

        function removeFilter(index) {
            vm.filters.splice(index, 1);
        }

        function addFilter() {
            var filter = {};
            angular.copy(defaultFilter, filter);
            filter.operator = Object.keys(getOperators(filter.attributeIndex))[0];
            vm.filters.push(filter);
        }

        function updateDeployments() {
            if (vm.expeditionId) {
                $http.get(vm.restUri + 'arms/projects/' + vm.expeditionId).then(
                    function (response) {
                        angular.extend(vm.deployments, response.data.deployments);
                    },
                    function (response) {
                        vm.error = "Error fetching deployments";
                    }
                )
            } else {
                vm.deployments.length = 0;
            }
        }

        function getFimsRestUri() {
            return $http.get('/settings/fims/').then(
                function (response) {
                    vm.restUri = response.data.uri;
                },
                function (response) {
                    vm.error = "Error fetching configuration.";
                }
            )
        }

        function getFilterOptions() {
            $http.get(vm.restUri + "projects/filterOptions").then(
                function (response) {
                    vm.filterOptions = response.data;
                    transformOperators();
                    addFilter();
                },
                function (response) {
                    vm.error = "Error fetching filter options";
                }
            )
        }

        function getArmsExpeditions() {
            $http.get(vm.restUri + 'arms/projects/')
                .then(function (response) {
                    angular.extend(vm.armsExpeditions, response.data);
                }, function (response, status) {
                    vm.error = "Error fetching projects";
                })
        }

        angular.element(document).ready(function () {
            jQuery('#deploymentModal').on('show.bs.modal', function (e) {
                var loadurl = jQuery(e.relatedTarget).data("load-url") + " #deploymentMetadata";
                var deployment = jQuery(e.relatedTarget).data("deployment-id");
                jQuery(this).find('.modal-title').html("Deployment: " + deployment);
                jQuery(this).find('.modal-body').html("loading...");
                jQuery(this).find('.modal-body').load(loadurl);
            });
        });

        (function init() {
            getFimsRestUri().then(
                function (response) {
                    getFilterOptions();
                    getArmsExpeditions();
                }
            );

        }).call();
    }]);
