angular.module('fims.expeditions')

.factory('ExpeditionFactory', ['$http', 'REST_ROOT', function ($http, REST_ROOT) {
    var expeditionFactory = {
        getExpeditions: getExpeditions,
        createExpedition: createExpedition
    };

    return expeditionFactory;

    function getExpeditions() {
        return $http.get(REST_ROOT + 'arms/projects/');
    }

    function createExpedition(expedition) {
        return $http.post(REST_ROOT + 'arms/projects', expedition);
    }
}]);