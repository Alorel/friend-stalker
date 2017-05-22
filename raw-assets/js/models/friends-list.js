import ko from "knockout";
import glob from "../model-global.js";
import $ from "jquery";
import PATH from "path";
import {error as toastError, info as toastInfo} from "../util/toast.js";
import {getFriendMarker} from "./map.js";

/*
 Model for the friends list
 */

const model = {
        friends: ko.observableArray([]),
        friendsBeingRefreshed: ko.observable(false)
    },
    fetchFriends = () => {
        model.friendsBeingRefreshed(true);
        $.getJSON(`${PATH}api/subscriptions/list-subscriptions`)
            .always($ajaxFetchFriendsAlways)
            .done($ajax$fetchFriends$done)
            .fail($ajax$fetchFriends$fail);
    },
    $friendsList = $("#friend-list"),
    $ajax$fetchFriends$done = r => {
        let d, tooltip;
        for (let user of r) {
            d = new Date(user.lastUpdated);

            tooltip = [
                `<small><strong>Name:</strong> <span>${user.id}</span></small>`,
                `<small><strong>Last update:</strong><time datetime="${d.toISOString()}">${d.toLocaleString()}</time></small>`
            ];

            if (user.location) {
                tooltip.push(
                    `<small><div><span class="label label-default">Lat</span></div><span>${user.location.latitude}</span></small>`,
                    `<small><div><span class="label label-default">Long</span></div><span>${user.location.longitude}</span></small>`
                );
            }

            tooltip = `<div class="table-like text-left vertical-middle">${tooltip.join("")}</div>`;

            if (user.location) {
                tooltip += '<div class="text-center text-info">Click to center map</div>';
            } else {
                tooltip += '<div class="text-center text-danger">Never checked in</div>';
            }

            user.tooltip = tooltip;
        }
        model.friends(r);
    },
    $ajaxFetchFriendsAlways = () => {
        model.friendsBeingRefreshed(false);
    },
    $ajax$fetchFriends$fail = () => {
        $ajax$fetchFriends$done([]);
    };

if (glob.loggedIn()) {
    fetchFriends();
}

glob.loggedIn.subscribe(loggedIn => {
    if (loggedIn) {
        fetchFriends();
    } else {
        model.friends([]);
    }
});

$("#refresh-friends").click(fetchFriends);

const friendsListMutationObserver = new MutationObserver(mutations => {
    for (let mutation of mutations) {
        if (mutation.addedNodes.length) {
            for (let node of mutation.addedNodes) {
                if (node instanceof HTMLDivElement) {
                    $(node).tooltip({html: true});
                }
            }
        }
    }
});
friendsListMutationObserver.observe($friendsList[0], {
    childList: true
});

$friendsList.on('click', '>.list-group-item', function () {
    const id = $(this).data('user-id');

    if (!id) {
        toastError("Couldn't determine the user's ID... have you been doing weird stuff with the page's HTML..?");
    } else {
        const marker = getFriendMarker(id);

        if (!marker) {
            toastInfo(`${id} hasn't checked in anywhere yet...`);
        } else {
            marker.panTo();
        }
    }
});

export default model;