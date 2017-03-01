(function ($, window, Drupal, drupalSettings) {
    'use strict';

    Drupal.AjaxCommands.prototype.redirect = function(ajax, response, status) {
        if (response.newWindow) {
            window.open(response.url);
        } else {
            window.location = response.url;
        }
    }
})(jQuery, this, Drupal, drupalSettings);