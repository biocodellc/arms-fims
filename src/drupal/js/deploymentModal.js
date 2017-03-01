jQuery('#deploymentModal').on('show.bs.modal', function (e) {
    var loadurl = jQuery(e.relatedTarget).data("load-url") + " #deploymentMetadata";
    var deployment = jQuery(e.relatedTarget).data("deployment-id");
    jQuery(this).find('.modal-title').html("Deployment: " + deployment);
    jQuery(this).find('.modal-body').html("loading...");
    jQuery(this).find('.modal-body').load(loadurl);
});