(function ($, window, Drupal, drupalSettings) {
    'use strict';

    Drupal.AjaxCommands.prototype.bootstrapSortable = function(ajax, response, status) {
        $.bootstrapSortable(response.options);
    }
})(jQuery, this, Drupal, drupalSettings);