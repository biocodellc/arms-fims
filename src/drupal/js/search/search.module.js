var app = angular.module('armsSearchApp', ['ui.bootstrap']);

app.controller('searchCtrl', ['$scope', '$filter', '$window', 'DataFactory',
    function ($scope, $filter, $window, DataFactory) {
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
        vm.getList = getList;
        vm.query = query;
        vm.excel = excel;
        vm.map = map;
        vm.formatDateTime = formatDateTime;
        vm.isDate = isDate;
        vm.getDateFormat = getDateFormat;
        vm.openDatePopup = openDatePopup;
        vm.getDatePickerMode = getDatePickerMode;
        vm.getPlaceholder = getPlaceholder;
        vm.getPI = getPI;

        function getPI(expeditionId) {
            angular.forEach(vm.armsExpeditions, function(expedition) {
                if (expedition.expeditionId == expeditionId) {
                    return expedition.principalInvestigator;
                }
            })

            return "";
        }

        function getPlaceholder(attributeIndex) {
            if (vm.filterOptions.attributes[attributeIndex].dataformat) {
                return "ex." + vm.filterOptions.attributes[attributeIndex].dataformat;
            }
            return "";
        }

        function getDatePickerMode(attributeIndex) {
            if (getDateFormat(attributeIndex).indexOf("d") !== -1) {
                return "day";
            }
            return "month";
        }

        function openDatePopup(filter) {
            filter.datePopupOpened = true;
        }

        function getDateFormat(attributeIndex) {
            // ISO8061 formats use capital Y and D, however uib dateparser uses lowercase y and d, so we need to replace
            return vm.filterOptions.attributes[attributeIndex].dataformat.replace(/Y/g, "y").replace(/D/g, "d");
        }

        function isDate(attributeIndex) {
            return vm.filterOptions.attributes[attributeIndex].datatype == "DATE";
        }

        function map() {
            var criterions = JSON.stringify({"criterion": getFilterCriterion()});
            DataFactory.queryMap(criterions)
                .then(
                    function (response) {
                        if (response.data.url) {
                            $window.open(response.data.url);
                            resetError();
                        } else {
                            vm.error = "Error fetching query information for map";
                        }
                    }, function (response) {
                        vm.error = "Error fetching query information for map";
                    });
        }

        function excel() {
            var criterions = JSON.stringify({"criterion": getFilterCriterion()})
            DataFactory.queryExcel(criterions)
                .then(
                    function (response) {
                        if (response.data.url) {
                            $window.open(response.data.url);
                            resetError();
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
                angular.forEach(vm.filterOptions.attributes, function (attribute, index) {
                    if (attribute.column == column) {
                        value = $filter('date')(dateTime, getDateFormat(index));
                    }
                });
            }

            return value;
        }

        function query() {
            var criterions = JSON.stringify({"criterion": getFilterCriterion()});
            DataFactory.queryJson(criterions)
                .then(
                    function (response) {
                        vm.queryResults = response.data;
                        jQuery.bootstrapSortable();
                        resetError();
                    }, function (response) {
                        vm.error = "Error fetching query results.";
                    });
        }

        function getFilterCriterion() {
            var filters = [];
            angular.forEach(vm.filters, function (filter) {
                if (filter.value) {
                    var value = filter.value;

                    if (isDate(filter.attributeIndex)) {
                        value = $filter('date')(value, getDateFormat(filter.attributeIndex));
                    }

                    filters.push({
                        "key": vm.filterOptions.attributes[filter.attributeIndex].column_internal,
                        "operator": filter.operator,
                        "value": value,
                        "condition": "AND"
                    });
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

            if (vm.expeditionId && vm.expeditionId != "all") {
                filters.push({
                    "key": "armsExpedition",
                    "operator": "EQUALS",
                    "value": vm.expeditionId,
                    "condition": "AND"
                });
            }

            return filters;
        }

        function getOperators(attributeIndex) {
            var dataType = vm.filterOptions.attributes[attributeIndex].datatype;
            if (vm.filterOptions.attributes[attributeIndex].list) {
                // if the attribute has a list, then we only want to return =
                return {"EQUALS": "="}
            } else {
                return displayOperators[dataType];
            }
        }

        function getList(attributeIndex) {
            return vm.filterOptions.attributes[attributeIndex].list;
        }

        // We need to map the operators we fetched from fims into a map with the operators we want to display.
        // ex. Fims Data is "Equals" we map that to =. operatorMap object holds our mapping.
        // We can't do this in getOperators function due to angular databinding and infinite loop
        function transformOperators() {
            angular.forEach(vm.filterOptions.operators, function (operators, dataType) {
                var operatorsForDataType = {};
                angular.forEach(operators, function (operator) {
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
                DataFactory.getDeployments(vm.expeditionId)
                    .then(
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

        function getFilterOptions() {
            DataFactory.getFilterOptions()
                .then(
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
            DataFactory.getArmsExpeditions()
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

        function resetError() {
            vm.error = null;
        }

        $scope.$watch(function () {
            return DataFactory.error
        }, function (value) {
            vm.error = value;
        });

        $scope.$watch(function () {
            return DataFactory.ready
        }, function (ready) {
            if (ready) {
                getFilterOptions();
                getArmsExpeditions();
            }
        });

    }
]);

app.controller('searchMapCtrl', ['$scope', 'DataFactory',
    function ($scope, DataFactory) {
        var LATITUDE_COLUMN = 'decimalLatitude';
        var LONGITUDE_COLUMN = 'decimalLongitude';
        // var map = null;

        var vm = this;
        vm.map = null;
        vm.error

        function getAccessToken() {
            DataFactory.getMapboxAccessToken()
                .then(
                    function (response) {
                        createMap(response.data.accessToken);
                    }, function (response) {
                        vm.error = response.data.error || response.data.usrMessage || "Error loading map!";
                    }
                );
        }

        function getMarkers() {
            DataFactory.queryJson({"criterion": []})
                .then(
                    function (response) {
                        addDeploymentsToMap(response.data);
                    }, function (response) {
                        vm.error = response.data.error || response.data.usrMessage || "Error loading deployments!";
                        vm.map.spin(false);
                    }
                )
        }

        function createMap(accessToken) {
            vm.map = L.map('searchMap', {
                center: [0, 0],
                zoom: 1
            });
            vm.map.spin(true);
            L.tileLayer('https://api.mapbox.com/v4/mapbox.outdoors/{z}/{x}/{y}.png?access_token={access_token}',
                {access_token: accessToken})
                .addTo(vm.map);
        }

        function addDeploymentsToMap(deployments) {
            var markers = [];
            angular.forEach(deployments, function (deployment) {
                var lat = deployment[LATITUDE_COLUMN];
                var lng = deployment[LONGITUDE_COLUMN];

                var deploymentMarker = L.marker([lat, lng]);
                var detailsUrl = '/deployments/' + deployment.expeditionId + '/' + deployment.deploymentId;
                deploymentMarker.bindPopup(
                    "ProjectID: " + deployment.expeditionId +
                    "<br>DeploymentID: " + deployment.deploymentId +
                    "<br><a href='" + detailsUrl + "'>deployment details</a>"
                );

                markers.push(deploymentMarker);
            });

            var clusterLayer = L.markerClusterGroup()
                .addLayers(markers);
            var bounds = clusterLayer.getBounds();

            vm.map
                .addLayer(clusterLayer)
                .fitBounds(bounds)
                .setMaxBounds(bounds)
                .setMinZoom(1)
                .spin(false);
        }

        $scope.$watch(function () {
            return DataFactory.ready
        }, function (ready) {
            if (ready) {
                getAccessToken();
                getMarkers();
            }
        });
    }
]);

app.factory('DataFactory', ['$http',
    function ($http) {
        var REST_ROOT;

        (function init() {
            getFimsRestRoot();
        }).call(this);

        var DataFactory = {
            getMapboxAccessToken: getMapboxAccessToken,
            getArmsExpeditions: getArmsExpeditions,
            getFilterOptions: getFilterOptions,
            getDeployments: getDeployments,
            queryJson: queryJson,
            queryMap: queryMap,
            queryExcel: queryExcel,
            error: null,
            ready: false
        };

        return DataFactory;

        function getFimsRestRoot() {
            return $http.get('/settings/fims/').then(
                function (response) {
                    REST_ROOT = response.data.uri;
                    DataFactory.ready = true;
                },
                function (response) {
                    DataFactory.error = "Error fetching configuration.";
                    DataFactory.ready = false;
                }
            )
        }

        function getMapboxAccessToken() {
            return $http.get(REST_ROOT + 'utils/getMapboxToken');
        }

        function getArmsExpeditions() {
            return $http.get(REST_ROOT + 'arms/projects');
        }

        function getFilterOptions() {
            return $http.get(REST_ROOT + "projects/filterOptions");
        }

        function getDeployments(expeditionId) {
            return $http.get(REST_ROOT + 'arms/projects/' + expeditionId);
        }

        function queryJson(criterions) {
            return $http.post(REST_ROOT + "deployments/query/json", criterions);
        }

        function queryMap(criterions) {
            return $http.post("/deployments/search/map", criterions);
        }

        function queryExcel(criterions) {
            return $http.post("/deployments/search/excel", criterions);
        }
    }]);
