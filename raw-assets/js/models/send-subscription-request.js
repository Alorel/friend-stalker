import ko from "knockout";
import PATH from "path";
import $ from "jquery";
import outputXhrError from "../util/output-xhr-error.js";
import glob from "../model-global.js";

/*
 Model for sending a subscription request
 */

const model = {
    subscriptionRequestRecipient: ko.observable(""),
    subscriptionRequestMessage: ko.observable("Please input a username"),
    subscriptionRequestClass: ko.observable("text-danger"),
    subscriptionRequestSending: ko.observable(false),
    subscriptionRequestButtonEnabled: ko.pureComputed(() => {
        const recipient = model.subscriptionRequestRecipient();
        return !model.subscriptionRequestSending()
            && recipient.length
            && recipient !== glob.username();
    })
}, $subscribe$logIn = newValue => {
    if (newValue) {
        model.subscriptionRequestRecipient("");
    }
};

$subscribe$logIn(glob.loggedIn()); // Set initial value
glob.loggedIn.subscribe($subscribe$logIn);

model.subscriptionRequestRecipient.subscribe(newValue => {
    let msg = "",
        css = "text-danger";

    if (!newValue.length) {
        msg = "Please input a username";
    } else if (newValue === glob.username()) {
        msg = "You can't subscribe to yourself, silly goose...";
    } else {
        msg = "";
        css = "text-primary";
    }

    model.subscriptionRequestClass(css);
    model.subscriptionRequestMessage(msg);
});

function $ajax$always$sendSubscriptionRequest() {
    model.subscriptionRequestSending(false);
}

function $ajax$done$sendSubscriptionRequest() {
    model.subscriptionRequestMessage("Subscription request sent!");
    model.subscriptionRequestClass("text-success");
}

function $ajax$fail$sendSubscriptionRequest(xhr, status, error) {
    outputXhrError(model.subscriptionRequestMessage, model.subscriptionRequestClass, error, xhr);
}

$("#send-subscription-request").submit(e => {
    e.preventDefault();
    const recipient = model.subscriptionRequestRecipient();

    model.subscriptionRequestSending(true);
    $.post(`${PATH}api/subscriptions/subscribe/${encodeURIComponent(recipient)}`)
        .always($ajax$always$sendSubscriptionRequest)
        .done($ajax$done$sendSubscriptionRequest)
        .fail($ajax$fail$sendSubscriptionRequest);
});

export default model;