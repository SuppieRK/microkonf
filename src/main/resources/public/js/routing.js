function toggleContentVisibility(targetPageClassName) {
    let targetPage = $('div.' + targetPageClassName);

    if (targetPage.css('display') !== 'block') {
        $('div.page').each(function () {
            $(this).css('display', 'none');
        })
        targetPage.css('display', 'block');
    }
}

function switchPage(targetPageClassName) {
    if (targetPageClassName === 'home') {
        history.pushState({'page': targetPageClassName}, null, './')
    } else {
        history.pushState({'page': targetPageClassName}, null, './' + targetPageClassName)
    }

    toggleContentVisibility(targetPageClassName)

    return false;
}

window.addEventListener('popstate', function (event) {
    if (event.state === undefined || event.state === null || event.state.page === 'home') {
        toggleContentVisibility('home')
    } else {
        toggleContentVisibility(event.state.page)
    }
    event.preventDefault();
});

// TODO Not working yet
// $(document).on('show.bs.modal', function (event) {
//     if (!event.relatedTarget) {
//         $('.modal').not(event.target).modal('hide');
//     }
//
//     if ($(event.relatedTarget).parents('.modal').length > 0) {
//         $(event.relatedTarget).parents('.modal').modal('hide');
//     }
// });