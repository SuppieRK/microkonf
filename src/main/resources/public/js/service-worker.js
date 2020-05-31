self.addEventListener('install', event => {
    console.log('Service Worker installed!')
});

self.addEventListener('activate', event => {
    console.log('Service Worker is now active!')
});

// TODO add caching for our pages
self.addEventListener('fetch', event => {
    // EXAMPLE, REMOVE LATER
    const url = new URL(event.request.url);
    if (url.origin === location.origin && url.pathname === '/dog.svg') {
        event.respondWith(caches.match('/cat.svg'));
    }
});