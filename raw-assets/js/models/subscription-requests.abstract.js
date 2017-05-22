import ko from "knockout";
import $ from "jquery";
import glob from "../model-global.js";
import PATH from "path";

/*
 Abstraction for incoming and outgoing subscription requests
 */

export default (arrayKey, refreshStateKey, type, refreshButton) => {
    const model = {};
    model[arrayKey] = ko.observableArray([]);
    model[refreshStateKey] = ko.observable(false);

    function listSubscriptionRequests() {
        model[refreshStateKey](true);
        $.getJSON(`${PATH}api/subscriptions/list-requests/${type}`)
            .done($ajax$done$listSubscriptionRequests)
            .fail($ajax$fail$listSubscriptionRequests);
    }

    function $ajax$done$listSubscriptionRequests(r) {
        model[arrayKey](r);
        model[refreshStateKey](false);
    }

    function $ajax$fail$listSubscriptionRequests() {
        $ajax$done$listSubscriptionRequests([]);
    }

    refreshButton.click(listSubscriptionRequests);

    function subscribe$logIn(val) {
        if (val) {
            listSubscriptionRequests();
        } else {
            model[arrayKey].removeAll();
        }
    }

    subscribe$logIn(glob.loggedIn()); // Trigger initial value
    glob.loggedIn.subscribe(subscribe$logIn);

    return model;
}