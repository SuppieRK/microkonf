'use strict';

importScripts('/public/js/sw-toolbox.js');

var URLS_TO_CACHE = [
    '/',
    '/index.html',
    '/public/js/bootstrap.min.js',
    '/public/js/flickity.min.js',
    '/public/js/jquery.min.js',
    '/public/js/custom.js',
    '/public/js/lazysizes.min.js',
    '/public/js/popper.min.js',
    '/public/js/routing.js',
    '/public/js/sidenav.js',
    '/public/js/service-worker.js',
    '/public/js/service-worker-main.js',
    '/public/css/bootstrap.min.css',
    '/public/css/font-awesome.min.css',
    '/public/css/flickity.min.css',
    '/public/css/custom.css',
    '/public/fonts/fontawesome-webfont.woff2?v=4.7.0',
    '/public/images/logos/ankudinovka.png',
    '/public/images/logos/arcadia.jpg',
    '/public/images/logos/arcadia.png',
    '/public/images/logos/epam-systems.png',
    '/public/images/logos/five9.png',
    '/public/images/logos/google.svg',
    '/public/images/logos/humans.jpg',
    '/public/images/logos/jetbrains.svg',
    '/public/images/logos/kaspersky.png',
    '/public/images/logos/kaspersky.svg',
    '/public/images/logos/neuron.png',
    '/public/images/organizers/gdg.jpg',
    '/public/images/organizers/google.svg',
    '/public/images/organizers/kug.jpg',
    '/public/images/gallery/IMG_6619.jpg',
    '/public/images/gallery/IMG_8169.jpg',
    '/public/images/gallery/IMG_8186.jpg',
    '/public/images/gallery/IMG_8279.jpg',
    '/public/images/gallery/IMG_8402.jpg',
    '/public/images/gallery/IMG_8476.jpg',
    '/public/images/gallery/IMG_8556.jpg',
    '/public/images/gallery/IMG_8650.jpg',
    '/public/images/gallery/IMG_9379.jpg',
    '/public/images/gallery/IMG_9492.jpg',
    '/public/images/assets/ankudinovka-venue.jpg',
    '/public/images/assets/event-logo.svg',
    '/public/images/assets/event-logo-monochrome.svg',
    '/public/images/assets/manifest/icons/icon-48.png',
    '/public/images/assets/manifest/icons/icon-72.png',
    '/public/images/assets/manifest/icons/icon-96.png',
    '/public/images/assets/manifest/icons/icon-144.png',
    '/public/images/assets/manifest/icons/icon-192.png',
    '/public/images/assets/manifest/icons/icon-512.png',
    '/public/images/people/aleksandr_denisov.jpg',
    '/public/images/people/alexander_davydov.jpg',
    '/public/images/people/andrey_beryukhov.jpg',
    '/public/images/people/daniil_emelyanov.jpg',
    '/public/images/people/denis_aleksandrov.png',
    '/public/images/people/denis_ustavschikov.jpg',
    '/public/images/people/ekaterina_maksutova.jpg',
    '/public/images/people/ilya_bulatov.jpg',
    '/public/images/people/kirill_rozov.jpg',
    '/public/images/people/leonid_startsev.jpg',
    '/public/images/people/placeholder.png',
    '/public/images/people/roman_khlebnov.jpg'
];

toolbox.precache(URLS_TO_CACHE);

toolbox.router.get('/*', toolbox.networkFirst, { networkTimeoutSeconds: 5});
toolbox.router.get('/js/*', toolbox.cacheFirst);
toolbox.router.get('/css/*', toolbox.cacheFirst);
toolbox.router.get('/fonts/*', toolbox.cacheFirst);
toolbox.router.get('/images/*', toolbox.cacheFirst);
toolbox.router.get('/webfonts/*', toolbox.cacheFirst);