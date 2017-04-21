(function () {
    'use strict';

    angular.module('fims.home')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'AuthFactory'];

    function HomeController($scope, AuthFactory) {
        var vm = this;
        vm.isAuthenticated = AuthFactory.isAuthenticated;

        $scope.$watch(
            function () {
                return AuthFactory.isAuthenticated
            },

            function (newVal) {
                vm.isAuthenticated = newVal;
            }
        );
    }

}).call();