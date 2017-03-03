jQuery("#overview").on(
    'shown.bs.collapse',
    function (e) {
        var width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
        if (width >= 768) { // only do this when the content is in 2 or 3 columns. strange things happen on smaller devices
            e.currentTarget.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    }
);