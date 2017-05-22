import ko from "knockout";
import $ from "jquery";
import mGlobal from "model-global.js";
import mLogin from "models/login.js";
import mSendSubRequest from "models/send-subscription-request.js";
import mIncSubRequests from "models/incoming-subscription-requests.js";
import mOutSubRequests from "models/outgoing-subscription-requests.js";
import mCheckIn from "models/check-in.js";
import mFriendsList from "models/friends-list.js";

import "./bindings/longdate.js";
import "./bindings/slide-visible.js";
import "./bindings/visibility.js";

import "models/map.js";

/*
 Main module
 */

const models = [
    {},
    mGlobal,
    mLogin,
    mSendSubRequest,
    mIncSubRequests,
    mOutSubRequests,
    mCheckIn,
    mFriendsList
];

const finalModel = $.extend.apply($, models);
ko.applyBindings(finalModel);

export default finalModel;