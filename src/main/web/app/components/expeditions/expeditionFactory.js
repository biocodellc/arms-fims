angular.module('fims.expeditions')

.factory('ExpeditionFactory', ['$http', 'REST_ROOT', function ($http, REST_ROOT) {
    var expeditionFactory = {
        getExpeditions: getExpeditions,
        createExpedition: createExpedition
    };

    return expeditionFactory;

    function getExpeditions(includePublic) {
        return $http.get(REST_ROOT + 'arms/projects/?includePublic=' + includePublic);
    }

    function createExpedition(expedition) {
        return $http.post(REST_ROOT + 'arms/projects', expedition);
    }
}]);