angular.module('armsApp')

    .constant("PROJECT_ID", 30)
    .constant("REST_ROOT", "/arms/rest/v1.1/")
    .constant("ID_REST_ROOT", "/arms/id/v1.1/")
    // When changing this, also need to change <base> tag in index.html
    .constant("APP_ROOT", "/arms/");
