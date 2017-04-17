/* ====== General Utility Functions ======= */
var appRoot = "/";
var armsFimsRestRoot = "/rest/v1/";

$.ajaxSetup({
    beforeSend: function (jqxhr, config) {
        jqxhr.config = config;
        var armsSessionStorage = JSON.parse(window.sessionStorage.arms);
        var accessToken = armsSessionStorage.accessToken;
        if (accessToken && config.url.indexOf("access_token") == -1) {
            if (config.url.indexOf('?') > -1) {
                config.url += "&access_token=" + accessToken;
            } else {
                config.url += "?access_token=" + accessToken;
            }
        }
    }
});

$.ajaxPrefilter(function (opts, originalOpts, jqXHR) {
    // you could pass this option in on a "retry" so that it doesn't
    // get all recursive on you.
    if (opts.refreshRequest) {
        return;
    }

    if (opts.url.indexOf('/validate') > -1) {
        return;
    }

    if (typeof(originalOpts) != "object") {
        originalOpts = opts;
    }

    // our own deferred object to handle done/fail callbacks
    var dfd = $.Deferred();

    // if the request works, return normally
    jqXHR.done(dfd.resolve);

    // if the request fails, do something else
    // yet still resolve
    jqXHR.fail(function () {
        var args = Array.prototype.slice.call(arguments);
        var armsSessionStorage = JSON.parse(window.sessionStorage.arms);
        var refreshToken = armsSessionStorage.refreshToken;
        if ((jqXHR.status === 401 || (jqXHR.status === 400 && jqXHR.responseJSON.usrMessage == "invalid_grant"))
            && !isTokenExpired() && refreshToken) {
            $.ajax({
                url: armsFimsRestRoot + 'authenticationService/oauth/refresh',
                method: 'POST',
                refreshRequest: true,
                data: $.param({
                    client_id: client_id,
                    refresh_token: refreshToken
                }),
                error: function () {
                    window.sessionStorage.arms = JSON.stringify({});

                    // reject with the original 401 data
                    dfd.rejectWith(jqXHR, args);

                    if (!window.location.pathname == appRoot)
                        window.location = appRoot + "login";
                },
                success: function (data) {
                    var armsSessionStorage = {
                        accessToken: data.access_token,
                        refreshToken: data.refresh_token,
                        oAuthTimestamp: new Date().getTime()
                    };

                    window.sessionStorage.arms = JSON.stringify(armsSessionStorage);

                    // retry with a copied originalOpts with refreshRequest.
                    var newOpts = $.extend({}, originalOpts, {
                        refreshRequest: true,
                        url: originalOpts.url.replace(/access_token=.{20}/, "access_token=" + data.access_token)
                    });
                    // pass this one on to our deferred pass or fail.
                    $.ajax(newOpts).then(dfd.resolve, dfd.reject);
                }
            });

        } else {
            dfd.rejectWith(jqXHR, args);
        }
    });

    // NOW override the jqXHR's promise functions with our deferred
    return dfd.promise(jqXHR);
});

function isTokenExpired() {
    var armsSessionStorage = JSON.parse(window.sessionStorage.arms);
    var oAuthTimestamp = armsSessionStorage.oAuthTimestamp;
    var now = new Date().getTime();

    if (now - oAuthTimestamp > 1000 * 60 * 60 * 4)
        return true;

    return false;
}

// function to retrieve a user's projects and populate the page
function listProjects(username, url, expedition) {
    var jqxhr = $.getJSON(url
    ).done(function (data) {
        if (!expedition) {
            var html = '<h1>Project Manager (' + username + ')</h2>\n';
        } else {
            var html = '<h1>Expedition Manager (' + username + ')</h2>\n';
        }
        var expandTemplate = '<br>\n<a class="expand-content" id="{project}-{section}" href="javascript:void(0);">\n'
            + '\t <img src="' + appRoot + 'css/images/right-arrow.png" id="arrow" class="img-arrow">{text}'
            + '</a>\n';
        $.each(data, function (index, element) {
            key = element.projectId;
            val = element.projectTitle;
            var project = val.replace(new RegExp('[#. ()]', 'g'), '_') + '_' + key;

            html += expandTemplate.replace('{text}', element.projectTitle).replace('-{section}', '');
            html += '<div id="{project}" class="toggle-content">';
            if (!expedition) {
                html += expandTemplate.replace('{text}', 'Project Metadata').replace('{section}', 'metadata').replace('<br>\n', '');
                html += '<div id="{project}-metadata" class="toggle-content">Loading Project Metadata...</div>';
                html += expandTemplate.replace('{text}', 'Project Expeditions').replace('{section}', 'expeditions');
                html += '<div id="{project}-expeditions" class="toggle-content">Loading Project Expeditions...</div>';
                html += expandTemplate.replace('{text}', 'Project Users').replace('{section}', 'users');
                html += '<div id="{project}-users" class="toggle-content">Loading Project Users...</div>';
            } else {
                html += 'Loading...';
            }
            html += '</div>\n';

            // add current project to element id
            html = html.replace(new RegExp('{project}', 'g'), project);
        });
        if (html.indexOf("expand-content") == -1) {
            if (!expedition) {
                html += 'You are not an admin for any project.';
            } else {
                html += 'You do not belong to any projects.'
            }
        }
        $(".sectioncontent").html(html);

        // store project id with element, so we don't have to retrieve project id later with an ajax call
        $.each(data, function (index, element) {
            key = element.projectId;
            val = element.projectTitle;
            var project = val.replace(new RegExp('[#. ()]', 'g'), '_') + '_' + key;

            if (!expedition) {
                $('div#' + project + '-metadata').data('projectId', key);
                $('div#' + project + '-users').data('projectId', key);
                $('div#' + project + '-expeditions').data('projectId', key);
            } else {
                $('div#' + project).data('projectId', key);
            }
        });
    }).fail(function (jqxhr) {
        $(".sectioncontent").html(jqxhr.responseText);
    });
    return jqxhr;

}

function getQueryParam(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            if (sParam == "return_to") {
                // if we want the return_to query param, we need to return everything after "return_to="
                // this is assuming that "return_to" is the last query param, which it should be
                return decodeURIComponent(sPageURL.slice(sPageURL.indexOf(sParameterName[1])));
            } else {
                return decodeURIComponent(sParameterName[1]);
            }
        }
    }
}

// Populate Div element from a REST service with HTML
function populateDivFromService(url, elementID, failMessage) {
    if (elementID.indexOf('#') == -1) {
        elementID = '#' + elementID
    }
    return jqxhr = $.ajax(url, function () {
    })
        .done(function (data) {
            $(elementID).html(data);
        })
        .fail(function () {
            $(elementID).html(failMessage);
        });
}

// function to open a new or update an already open jquery ui dialog box
function dialog(msg, title, buttons) {
    var dialogContainer = $("#dialogContainer");
    if (dialogContainer.html() != msg) {
        dialogContainer.html(msg);
    }

    if (!$(".ui-dialog").is(":visible") || (dialogContainer.dialog("option", "title") != title ||
        dialogContainer.dialog("option", "buttons") != buttons)) {
        dialogContainer.dialog({
            modal: true,
            autoOpen: true,
            title: title,
            resizable: false,
            width: 'auto',
            draggable: false,
            buttons: buttons,
            position: {my: "center top", at: "top", of: window}
        });
    }

    return;
}

// A short message
function showMessage(message) {
    $('#alerts').append(
        '<div class="fims-alert alert alert-dismissable" role="alert">' +
        '<button type="button" class="close" data-dismiss="alert">' +
        '&times;</button>' + message + '</div>');
}

/* ====== expeditions.html Functions ======= */

// function to populate the expeditions.html page
function populateExpeditionPage(username) {
    var jqxhr = listProjects(username, armsFimsRestRoot + 'projects/user/list', true
    ).done(function () {
        // attach toggle function to each project
        $(".expand-content").click(function () {
            loadExpeditions(this.id)
        });
    }).fail(function (jqxhr) {
        $("#sectioncontent").html(jqxhr.responseText);
    });
}

// function to load the expeditions.html subsections
function loadExpeditions(id) {
    if ($('.toggle-content#' + id).is(':hidden')) {
        $('.img-arrow', '#' + id).attr("src", appRoot + "css/images/down-arrow.png");
    } else {
        $('.img-arrow', '#' + id).attr("src", appRoot + "css/images/right-arrow.png");
    }
    // check if we've loaded this section, if not, load from service
    var divId = 'div#' + id
    if ((id.indexOf("resources") != -1 || id.indexOf("configuration") != -1) &&
        ($(divId).children().length == 0)) {
        populateExpeditionSubsections(divId);
    } else if ($(divId).children().length == 0) {
        listExpeditions(divId);
    }
    $('.toggle-content#' + id).slideToggle('slow');
}

// retrieve the expeditions for a project and display them on the page
function listExpeditions(divId) {
    var projectId = $(divId).data('projectId');
    var jqxhr = $.getJSON(armsFimsRestRoot + 'projects/' + projectId + '/expeditions?user')
        .done(function (data) {
            var html = '';
            var expandTemplate = '<br>\n<a class="expand-content" id="{expedition}-{section}" href="javascript:void(0);">\n'
                + '\t <img src="' + appRoot + 'css/images/right-arrow.png" id="arrow" class="img-arrow">{text}'
                + '</a>\n';
            $.each(data, function (index, e) {
                var expedition = e.expeditionTitle.replace(new RegExp('[#. ():]', 'g'), '_') + '_' + e.expeditionId;
                while (!expedition.match(/^[a-zA-Z](.)*$/)) {
                    expedition = expedition.substring(1);
                }

                html += expandTemplate.replace('{text}', e.expeditionTitle).replace('-{section}', '');
                html += '<div id="{expedition}" class="toggle-content">';
                html += expandTemplate.replace('{text}', 'Expedition Metadata').replace('{section}', 'configuration').replace('<br>\n', '');
                html += '<div id="{expedition}-configuration" class="toggle-content">Loading Expedition Metadata...</div>';
                html += expandTemplate.replace('{text}', 'Expedition Resources').replace('{section}', 'resources');
                html += '<div id="{expedition}-resources" class="toggle-content">Loading Expedition Resources...</div>';
                html += '</div>\n';

                // add current project to element id
                html = html.replace(new RegExp('{expedition}', 'g'), expedition);
            });
            html = html.replace('<br>\n', '');
            if (html.indexOf("expand-content") == -1) {
                html += 'You have no expeditions in this project.';
            }
            $(divId).html(html);
            $.each(data, function (index, e) {
                var expedition = e.expeditionTitle.replace(new RegExp('[#. ():]', 'g'), '_') + '_' + e.expeditionId;
                while (!expedition.match(/^[a-zA-Z](.)*$/)) {
                    expedition = expedition.substring(1);
                }

                $('div#' + expedition + '-configuration').data('expeditionId', e.expeditionId);
                $('div#' + expedition + '-resources').data('expeditionId', e.expeditionId);
            });

            // remove previous click event and attach toggle function to each project
            $(".expand-content").off("click");
            $(".expand-content").click(function () {
                loadExpeditions(this.id);
            });
        }).fail(function (jqxhr) {
            $(divId).html(jqxhr.responseText);
        });
}

// function to populate the expedition resources, or configuration subsection of expeditions.html
function populateExpeditionSubsections(divId) {
    // load config table from REST service
    var expeditionId = $(divId).data('expeditionId');
    if (divId.indexOf("resources") != -1) {
        var jqxhr = populateDivFromService(
            armsFimsRestRoot + 'expeditions/' + expeditionId + '/resourcesAsTable/',
            divId,
            'Unable to load this expedition\'s resources from server.');
    } else {
        var jqxhr = populateDivFromService(
            armsFimsRestRoot + 'expeditions/' + expeditionId + '/metadataAsTable/',
            divId,
            'Unable to load this expedition\'s configuration from server.');
    }
}

/* ====== profile.html Functions ======= */

// function to submit the user's profile editor form
function profileSubmit(divId) {
    if ($("input.pwcheck", divId).val().length > 0 && $(".label", "#pwindicator").text() == "weak") {
        $(".error", divId).html("password too weak");
    } else if ($("input[name='newPassword']").val().length > 0 &&
        ($("input[name='oldPassword']").length > 0 && $("input[name='oldPassword']").val().length == 0)) {
        $(".error", divId).html("Old Password field required to change your Password");
    } else {
        var postURL = armsFimsRestRoot + "users/profile/update/";
        var return_to = getQueryParam("return_to");
        if (return_to != null) {
            postURL += "?return_to=" + encodeURIComponent(return_to);
        }
        var jqxhr = $.post(postURL, $("form", divId).serialize(), 'json'
        ).done(function (data) {
            // if adminAccess == true, an admin updated the user's password, so no need to redirect
            if (data.adminAccess == true) {
                populateProjectSubsections(divId);
            } else {
                if (data.returnTo) {
                    $(location).attr("href", data.returnTo);
                } else {
                    var jqxhr2 = populateDivFromService(
                        armsFimsRestRoot + "users/profile/listAsTable",
                        "listUserProfile",
                        "Unable to load this user's profile from the Server")
                        .done(function () {
                            $("a", "#profile").click(function () {
                                getProfileEditor();
                            });
                        });
                }
            }
        }).fail(function (jqxhr) {
            var json = $.parseJSON(jqxhr.responseText);
            $(".error", divId).html(json.usrMessage);
        });
    }
}

// get profile editor
function getProfileEditor(username) {
    var jqxhr = populateDivFromService(
        armsFimsRestRoot + "users/profile/listEditorAsTable",
        "listUserProfile",
        "Unable to load this user's profile editor from the Server"
    ).done(function () {
        $(".error").text(getQueryParam("error"));
        $("#cancelButton").click(function () {
            var jqxhr2 = populateDivFromService(
                armsFimsRestRoot + "users/profile/listAsTable",
                "listUserProfile",
                "Unable to load this user's profile from the Server")
                .done(function () {
                    $("a", "#profile").click(function () {
                        getProfileEditor();
                    });
                });
        });
        $("#profile_submit").click(function () {
            profileSubmit('div#listUserProfile');
        });
    });
}

/* ====== templates.html Functions ======= */

function populate_bottom() {
    var selected = new Array();
    var listElement = document.createElement("ul");
    listElement.className = 'picked_tags';
    $("#checked_list").html(listElement);
    $("input:checked").each(function () {
        var listItem = document.createElement("li");
        listItem.className = 'picked_tags_li';
        listItem.innerHTML = ($(this).val());
        listElement.appendChild(listItem);
    });
}

function download_file(projectId) {
    var url = armsFimsRestRoot + 'projects/createExcel/';
    var input_string = '';
    // Loop through CheckBoxes and find ones that are checked
    $(".check_boxes").each(function (index) {
        if ($(this).is(':checked'))
            input_string += '<input type="hidden" name="fields" value="' + $(this).val() + '" />';
    });
    input_string += '<input type="hidden" name="projectId" value="' + projectId + '" />';

    // Pass the form to the server and submit
    $('<form action="' + url + '" method="post">' + input_string + '</form>').appendTo('body').submit().remove();
}

// for template generator, get the definitions when the user clicks on DEF
function populateDefinitions(column, projectId) {
    theUrl = armsFimsRestRoot + "projects/" + projectId + "/getDefinition/" + column;

    var jqxhr = $.ajax({
        type: "GET",
        url: theUrl,
        dataType: "html",
    })
        .done(function (data) {
            $("#definition").html(data);
        });
}

function populateColumns(targetDivId, projectId) {

    if (projectId != 0) {
        theUrl = armsFimsRestRoot + "projects/" + projectId + "/attributes/";

        var jqxhr = $.ajax({
            url: theUrl,
            async: false,
            dataType: 'html'
        }).done(function (data) {
            $(targetDivId).html(data);
        }).fail(function (jqXHR, textStatus) {
            if (textStatus == "timeout") {
                showMessage("Timed out waiting for response!");
            } else {
                showMessage("Error completing request!");
            }
        });

        $(".def_link").click(function () {
            populateDefinitions($(this).attr('name'), projectId);
        });
    }
}

function populateAbstract(targetDivId, projectId) {
    $(targetDivId).html("Loading ...");

    theUrl = armsFimsRestRoot + "projects/" + projectId + "/abstract/";

    var jqxhr = $.ajax({
        url: theUrl,
        async: false,
        dataType: 'json'
    }).done(function (data) {
        $(targetDivId).html(data.abstract + "<p>");
    }).fail(function (jqXHR, textStatus) {
        if (textStatus == "timeout") {
            showMessage("Timed out waiting for response!");
        } else {
            showMessage("Error completing request!");
        }
    });
}

function updateCheckedBoxes(configName, projectId) {
    if (configName == "Default") {
        populateColumns("#cat1", projectId);
    } else {
        $.getJSON(armsFimsRestRoot + "projects/" + projectId + "/getTemplateConfig/" + configName.replace(/\//g, "%2F")).done(function (data) {
            if (data.error != null) {
                showMessage(data.error);
                return;
            }
            // deselect all unrequired columns
            $(':checkbox').not(":disabled").each(function () {
                this.checked = false;
            });

            data.checkedOptions.forEach(function (uri) {
                $(':checkbox[data-uri="' + uri + '"]')[0].checked = true;
            });
        }).fail(function (jqXHR, textStatus) {
            if (textStatus == "timeout") {
                showMessage("Timed out waiting for response! Try again later or reduce the number of graphs you are querying. If the problem persists, contact the System Administrator.");
            } else {
                showMessage("Error fetching template configuration!");
            }
        });
    }
}
