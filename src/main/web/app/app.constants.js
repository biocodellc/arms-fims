angular.module('armsApp')

    .constant("PROJECT_ID", 30)
    .constant("REST_ROOT", "/rest/v1/")
    .constant("ID_REST_ROOT", "/id/v1/")
    // When changing this, also need to change <base> tag in index.html
    .constant("APP_ROOT", "/");
