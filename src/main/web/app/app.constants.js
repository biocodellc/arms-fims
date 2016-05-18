angular.module('armsApp')

    .constant("PROJECT_ID", 30)
    .constant("REST_ROOT", "/arms/rest/")
    // When changing this, also need to change <base> tag in index.html
    .constant("APP_ROOT", "/arms/");
