angular.module('armsApp')

.config(['$stateProvider', '$urlRouterProvider', '$urlMatcherFactoryProvider', '$locationProvider',
    function ($stateProvider, $urlRouterProvider, $urlMatcherFactoryProvider, $locationProvider) {

        // make trailing / optional
        $urlMatcherFactoryProvider.strictMode(false);

        $stateProvider
            .state('home', {
                url: "/",
                templateUrl: "app/components/home/home.html",
                controller: "HomeController as vm"
            })
            .state('login', {
                url: "/login",
                templateUrl: "app/components/auth/login.html",
                controller: "LoginCtrl as vm",
            })
            .state('validate', {
                url: "/validate",
                templateUrl: "app/components/validation/validation.html",
                controller: "ValidationCtrl as vm"
            })
            .state('resetPass', {
                url: "/resetPass",
                templateUrl: "app/components/users/resetPass.html",
                controller: "ResetPassCtrl as vm"
            })
            .state('reset', {
                url: "/reset",
                templateUrl: "app/components/users/reset.html",
            })
            .state('template', {
                url: "/template?projectId",
                templateUrl: "app/components/templates/templates.html",
                controller: "TemplateCtrl as templateVm"
            })
            .state('query', {
                url: "/query",
                templateUrl: "app/components/query/query.html",
                controller: "QueryCtrl as vm",
            })
            .state('profile', {
                url: "/secure/profile?error",
                templateUrl: "app/components/users/profile.html",
                controller: "UserCtrl as vm",
                loginRequired: true
            })
            .state('projects', {
                url: "/secure/projects",
                templateUrl: "app/components/projects/projects.html",
                controller: "ProjectCtrl as vm",
                loginRequired: true
            })
            .state('expeditionManager', {
                url: "/secure/expeditions",
                templateUrl: "app/components/expeditions/expeditions.html",
                controller: "ExpeditionCtrl as vm",
                loginRequired: true
            })
            .state('resourceTypes', {
                url: "/resourceTypes",
                templateUrl: "app/components/creator/resourceTypes.jsp",
                controller: "ResourceTypesCtrl as vm"
            })
            .state('notFound', {
                url: '*path',
                templateUrl: "app/partials/page-not-found.html"
            });

        $locationProvider.html5Mode(true);
    }]);
