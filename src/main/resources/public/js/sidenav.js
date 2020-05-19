$(document).ready(function () {
    var fixHeight = function () {
        $(".navbar-nav").css(
            "max-height",
            document.documentElement.clientHeight - 150
        );
    };

    fixHeight();

    $(window).resize(function () {
        fixHeight();
    });

    $(".navbar-toggler").on("click", function () {
        fixHeight();
    });

    $(".navbar-toggler, .overlay").on("click", function () {
        var header = $(".sticky-header");
        header.toggleClass("activeMobile", !header.hasClass("activeMobile"));

        $(".mobileMenu, .overlay").toggleClass("open");
    });
});
