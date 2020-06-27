if ('serviceWorker' in navigator) {
    window.addEventListener('load', function () {
        navigator.serviceWorker.register('/service-worker.js', {
            scope: '/'
        }).then((registration) => {
            console.log('Service Worker registered. Scope is:', registration.scope)
        }).catch(error => {
            console.error('Service Worker registration failed!', error)
        });
    });
}