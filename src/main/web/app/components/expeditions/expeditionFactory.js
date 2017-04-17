angular.module('fims.expeditions')

    .factory('ExpeditionFactory', ['$http', 'UserFactory', 'REST_ROOT', 'PROJECT_ID',
        function ($http, UserFactory, REST_ROOT, PROJECT_ID) {
            var expeditionFactory = {
                getExpeditionsForUser: getExpeditionsForUser,
                getExpeditions: getExpeditions,
                getExpedition: getExpedition,
                getExpeditionsForAdmin: getExpeditionsForAdmin,
                updateExpeditions: updateExpeditions,
                updateExpedition: updateExpedition,
                deleteExpedition: deleteExpedition,
                createArmsExpedition: createArmsExpedition,
                getArmsExpeditions: getArmsExpeditions
            };

            return expeditionFactory;

            function getArmsExpeditions(includePrivate) {
                return $http.get(REST_ROOT + 'arms/projects?includePrivate=' + includePrivate);
            }

            function getExpeditionsForUser(includePrivate) {
                if (includePrivate == null) {
                    includePrivate = false;
                }
                return $http.get(REST_ROOT + 'projects/' + PROJECT_ID + '/expeditions?user&includePrivate=' + includePrivate);
            }

            function getExpeditions() {
                return $http.get(REST_ROOT + 'projects/' + PROJECT_ID + '/expeditions');
            }

            function getExpedition(expeditionCode) {
                return $http.get(REST_ROOT + 'projects/' + PROJECT_ID + '/expeditions/' + expeditionCode);
            }

            function deleteExpedition(expeditionCode) {
                return $http.delete(REST_ROOT + 'projects/' + PROJECT_ID + '/expeditions/' + expeditionCode);
            }

            function createArmsExpedition(armsExpedition) {
                return $http.post(REST_ROOT + 'arms/projects', armsExpedition);
            }

            function getExpeditionsForAdmin(projectId) {
                return $http.get(REST_ROOT + 'projects/' + projectId + '/expeditions?admin');
            }

            function updateExpeditions(projectId, expeditions) {
                return $http({
                    method: 'PUT',
                    url: REST_ROOT + 'projects/' + projectId + "/expeditions",
                    data: expeditions,
                    keepJson: true
                });
            }

            function updateExpedition(projectId, expedition) {
                return $http({
                    method: 'PUT',
                    url: REST_ROOT + 'projects/' + projectId + "/expeditions/" + expedition.expeditionCode,
                    data: expedition,
                    keepJson: true
                });
            }

            // function downloadData(projectId, expeditionId) {
            //     var criterions = JSON.stringify(
            //         {
            //             "criterion": [{
            //                 "key": "deploymentId",
            //                 "operator": "IN",
            //                 "value": expeditionId,
            //                 "condition": "AND"
            //             }]
            //         });
            //     return $http({
            //         me
            //     }).then(function (response) {
            //         if (response.data.url) {
            //             $window.open(response.data.url);
            //         } else {
            //             vm.error = "Error downloading expedition";
            //         }
            //     }, function (response) {
            //         vm.error = "Error downloading expedition";
            //     })
            // }
        }]);