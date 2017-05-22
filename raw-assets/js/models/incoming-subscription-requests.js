import createModel from "./subscription-requests.abstract.js";
import $ from "jquery";
import {error as toastError} from "../util/toast.js";
import PATH from "path";

/*
 Model for incoming subscription requests
 */

const $refreshBtn = $("#refresh-inc-subscriptions"),
    $list = $("#inc-subscriptions-list"),
    model = createModel(
        'incSubscriptionRequests',
        'incSubscriptionRefreshed',
        "incoming",
        $refreshBtn
    ),
    refresh = () => {
        $refreshBtn.click();
    },
    slideAndRemoveCallback = () => {
        if (!$list.find(">li").length) {
            model.incSubscriptionRequests([]);
        }
    };

$list.on("click", '[data-act]', function () {
    const self = $(this),
        li = self.closest('li'),
        btns = li.find('[data-act]'),
        target = li.data("id"),
        type = self.data("act");

    btns.prop("disabled", true);

    $.ajax(`${PATH}api/subscriptions/respond/${encodeURIComponent(target)}`, {
        method: type === "accept" ? "PATCH" : "DELETE"
    }).fail((xhr, status, error) => {
        btns.prop("disabled", false);
        toastError(error || xhr.responseText || "Unknown error");
    }).done(() => {
        li.slideAndRemove(slideAndRemoveCallback);
    })
});

export default model;