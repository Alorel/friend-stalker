import $ from "jquery";
import Promise from "promise";
import {resolveMessage} from "../../util/xhr-toast-error-handler.js";
import ko from "knockout";
import {add as listen, remove as unlisten} from "../../util/passive-event-listener-with-fallback.js";

/*
 Need to do clever stuff here... the map doesn't load properly whilst in a
 closed tab, so we can only resolve the promise and create the map object once
 we know the tab is open!
 */

export default new Promise((resolve, reject) => {
    window.initMap = () => {
        delete window.initMap;
        const mapTab = $('#tabs').find('[data-toggle="tab"][href="#map"]');

        const doInit = () => {
            const $window = $(window),
                mapContainer = $("#map-container"),
                optimalSize = ko.observable("").extend({rateLimit: 250}),
                adjustMapSizeEventHandler = () => {
                    optimalSize(`${$window.height() - mapContainer.position().top}px`);
                };

            optimalSize.subscribe(optimalSize => mapContainer.css("height", optimalSize));

            adjustMapSizeEventHandler();
            mapTab.on('shown.bs.tab', () => {
                adjustMapSizeEventHandler();
                listen(window, 'resize', adjustMapSizeEventHandler);
            });
            mapTab.on('hidden.bs.tab', () => unlisten(window, 'resize', adjustMapSizeEventHandler));

            resolve(new google.maps.Map(mapContainer[0], {
                zoom: 8,
                center: {lat: 55.861122, lng: -4.250316},
                clickableIcons: true
            }));
        };

        if ($("#map").is(":visible")) {
            doInit();
        } else {
            mapTab.one('shown.bs.tab', doInit);
        }
    };

    $.getScript("https://maps.googleapis.com/maps/api/js?callback=initMap&key=AIzaSyDeX8pwegJD9QsGWG38Qdgxju-VjLkON-I")
        .fail((xhr, status, error) => reject(resolveMessage(xhr, status, error)));
});