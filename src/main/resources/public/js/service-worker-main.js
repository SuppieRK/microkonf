if ('serviceWorker' in navigator) {
  window.addEventListener('load', function() {
    navigator.serviceWorker.register('/public/js/service-worker.js')
        .then(() => {
            console.log('Service Worker registered!')
        })
        .catch(error => {
            console.error('Service Worker registration failed!', error)
        });
  });
}