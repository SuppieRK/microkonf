if ('serviceWorker' in navigator) {
  window.addEventListener('load', function() {
    navigator.serviceWorker.register('/public/service-worker.js')
        .then((registration) => {
            console.log('Service Worker registered. Scope is:', registration.scope)
        })
        .catch(error => {
            console.error('Service Worker registration failed!', error)
        });
  });
}