$(document).ready(function () {
    // Fix for routing.js, since this scripts loads after page loading is completed unlike routing.js
    let currentPage = window.location.pathname.replace(/\//g, "")
    if (currentPage === "") {
        switchPage("home")
    } else {
        switchPage(currentPage)
    }

    var fixHeight = function () {
        $(".navbar-nav").css(
            "max-height",
            document.documentElement.clientHeight - 150
        );
    };

    fixHeight();

    $(window).resize(function () {
        fixHeight();

        // If client dynamically resizes window in browser this if clause fixes following use case:
        // 1. Resize until mobile menu activates
        // 2. Open menu
        // 3. Resize back to the point where it deactivates
        // That behavior otherwise will leave menu in activated state, which is not desirable
        if (document.documentElement.clientWidth > 975) {
            $(".sticky-header").removeClass("activeMobile")
            $(".mobileMenu, .overlay").removeClass("open")

            let currentPage = window.location.pathname.replace(/\//g, "")
            if (currentPage === "") {
                $('.hiding-navbar-brand').css('display', 'none');
            } else {
                $('.hiding-navbar-brand').css('display', 'block');
            }
        } else {
            $('.hiding-navbar-brand').css('display', 'none');
        }
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
