importScripts('service-worker-toolbox.min.js');

fetch(
    "/cache"
).then(function (response) {
    return response.json()
}).then(function (urls) {
    toolbox.precache(urls);
});

toolbox.router.get('/*', toolbox.networkFirst, {networkTimeoutSeconds: 5});
toolbox.router.get('/js/*', toolbox.cacheFirst);
toolbox.router.get('/css/*', toolbox.cacheFirst);
toolbox.router.get('/fonts/*', toolbox.cacheFirst);
toolbox.router.get('/images/*', toolbox.cacheFirst);
toolbox.router.get('/webfonts/*', toolbox.cacheFirst);