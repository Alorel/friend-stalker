import createModel from "./subscription-requests.abstract.js";
import sendSubRequestModel from "./send-subscription-request.js";
import ko from "knockout";
import $ from "jquery";
import {error as toastError} from "../util/toast.js";
import PATH from "path";

/*
 Outgoing subscriptions model
 */

const refreshBtn = $("#refresh-out-subscriptions"),
    $list = $("#out-subscriptions-list"),
    model = createModel(
        'outSubscriptionRequests',
        'outSubscriptionRefreshed',
        "outgoing",
        refreshBtn
    ),
    successfullySentSubscriptionRequest = ko.pureComputed(() => {
        return !sendSubRequestModel.subscriptionRequestSending()
            && sendSubRequestModel.subscriptionRequestClass() === "text-success";
    }),
    refresh = () => {
        refreshBtn.click();
    },
    slideAndRemoveCallback = () => {
        if (!$list.find(">li").length) {
            model.outSubscriptionRequests([]);
        }
    };

successfullySentSubscriptionRequest.subscribe(newVal => {
    if (newVal) {
        refresh();
    }
});

$list.on("click", '[data-act="cancel"]', function () {
    const self = $(this),
        li = self.closest('li'),
        to = li.data('id');

    self.prop("disabled", true);

    $.ajax(`${PATH}api/subscriptions/request/${encodeURIComponent(to)}`, {
        method: 'DELETE'
    }).fail((xhr, status, error) => {
        self.prop("disabled", false);
        toastError(error || xhr.responseText || "Unknown error");
    }).done(() => {
        li.slideAndRemove(slideAndRemoveCallback);
    });
});

export default model;