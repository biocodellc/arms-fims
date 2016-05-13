angular.module('armsApp')

    .constant("PROJECT_ID", 25)
    .constant("REST_ROOT", "/arms-fims/rest/")
    // When changing this, also need to change <base> tag in index.html
    .constant("APP_ROOT", "/arms-fims/");
