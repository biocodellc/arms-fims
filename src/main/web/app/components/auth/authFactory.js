angular.module('fims.auth')

.factory('AuthFactory', ['$http', '$q', '$window', '$location', '$state', 'oAuth', 'REST_ROOT', 'APP_ROOT',
    function ($http, $q, $window, $location, $state, oAuth, REST_ROOT, APP_ROOT) {
        var triedToRefresh = false;

        var authFactory = {
            isAuthenticated: checkAuthenticated(),
            login: login,
            logout: logout,
            refreshAccessToken: refreshAccessToken,
            isTokenExpired: isTokenExpired,
            getAccessToken: getAccessToken
        };

        return authFactory;

        function checkAuthenticated() {
            return !isTokenExpired() && !angular.isUndefined(getAccessToken());
        }

        function isTokenExpired() {
            var armsSessionStorage = JSON.parse($window.sessionStorage.arms);
            var oAuthTimestamp = armsSessionStorage.oAuthTimestamp;
            var now = new Date().getTime();

            if (now - oAuthTimestamp > oAuth.USER_LOGIN_EXPIRATION) {
                logout();
                return true;
            }

            return false;
        }
        
        function getAccessToken() {
            var armsSessionStorage = JSON.parse($window.sessionStorage.arms);
            return armsSessionStorage.accessToken;
        }

        function login(username, password) {
            var config = {
                method: 'POST',
                url: REST_ROOT + 'authenticationService/oauth/accessToken',
                data: {
                    client_id: client_id,
                    redirect_uri: APP_ROOT + '/oauth',
                    grant_type: 'password',
                    username: username,
                    password: password
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            };

            return $http(config)
                .success(function(data, status, headers, config) {
                    setOAuthTokens(data.access_token, data.refresh_token);
                    authFactory.isAuthenticated = true;
                    triedToRefresh = false;
                })
                .error(function (data, status, headers, config) {
                    authFactory.logout();
                });
        }

        function logout() {
            $window.sessionStorage.arms = JSON.stringify({});
            if (authFactory)
                authFactory.isAuthenticated = false;
        }

        function refreshAccessToken() {
            var armsSessionStorage = JSON.parse($window.sessionStorage.arms);
            var refreshToken = armsSessionStorage.refreshToken;
            if (checkAuthenticated() && !triedToRefresh && !angular.isUndefined(refreshToken)) {
                var config = {
                    method: 'POST',
                    url: REST_ROOT + 'authenticationService/oauth/refresh',
                    data: {
                        client_id: client_id,
                        refresh_token: refreshToken
                    },
                };

                return $http(config)
                    .then(function(response) {
                        setOAuthTokens(response.data.access_token, response.data.refresh_token);
                        triedToRefresh = false;
                    },
                    function (response) {
                        triedToRefresh = true;
                        authFactory.isAuthenticated = false;
                    });
            }

            return $q.reject();
        }

        function setOAuthTokens(accessToken, refreshToken) {
            var armsSessionStorage = {
                accessToken: accessToken,
                refreshToken: refreshToken,
                oAuthTimestamp: new Date().getTime()
            };
            
            $window.sessionStorage.arms = JSON.stringify(armsSessionStorage);
        }
    }]);