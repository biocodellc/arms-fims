/**
 * @license Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

// Register a templates definition set named "default".
CKEDITOR.addTemplates('default', {
    // The name of sub folder which hold the shortcut preview images of the
    // templates.
    imagesPath: CKEDITOR.getUrl(CKEDITOR.plugins.getPath('templates') + 'templates/images/'),

    // The templates definitions.
    templates: [{
            title: 'Protocols Media List',
            image: '',
            description: 'Add a bootstrap <a class="link" style="text-decoration: underline;" target="_blank" href="http://getbootstrap.com/components/#media-list">Media List</a>.',
            html: '<ul class="media-list protocols-media-list"><li class="media"><div class="media-left">insert img here</div><div class="media-body"><h3 class="media-heading">Media heading</h3>media text</div></li></ul>'
        },
        {
            title: 'Protocols Media List Item',
            image: '',
            description: 'Add a bootstrap <a class="link" style="text-decoration: underline;" target="_blank" href="http://getbootstrap.com/components/#media-list">Media List Item</a>.',
            html: '<li class="media"><div class="media-left">img</div><div class="media-body"><h3 class="media-heading">Media heading</h3>media text</div></li>'
        }
    ]
});
