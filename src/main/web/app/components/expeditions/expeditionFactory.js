angular.module('fims.expeditions')

.factory('ExpeditionFactory', ['$http', 'REST_ROOT', function ($http, REST_ROOT) {
    var expeditionFactory = {
        getExpeditions: getExpeditions
    }

    return expeditionFactory;

    function getExpeditions(projectId) {
        return $http.get(REST_ROOT + 'projects/' + projectId + '/expeditions');
    }
}]);