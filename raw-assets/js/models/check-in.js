import ko from "knockout";
import $ from "jquery";
import PATH from "path";
import qs from "../util/format-querystring.js";
import {success as toastSuccess} from "../util/toast.js";
import glob from "../model-global.js";
import toastErrorHandler from "../util/xhr-toast-error-handler.js";

/*
 * Model for the the check-in process
 */

const model = {
        checkInLatitude: ko.observable(""),
        checkInLongitude: ko.observable(""),
        checkInLatValid: ko.pureComputed(() => coordinateIsValid(model.checkInLatitude(), MAX_LAT)),
        checkInLongValid: ko.pureComputed(() => coordinateIsValid(model.checkInLongitude(), MAX_LNG)),
        checkInFormValid: ko.pureComputed(() => model.checkInLatValid() && model.checkInLongValid()),
        checkInLatClass: ko.pureComputed(() => model.checkInLatValid() ? 'success' : 'danger'),
        checkInLongClass: ko.pureComputed(() => model.checkInLongValid() ? 'success' : 'danger'),
        checkingIn: ko.observable(false),
        checkInBtnEnabled: ko.pureComputed(() => model.checkInFormValid() && !model.checkingIn()),
        lastCheckIn: {
            lat: ko.observable(""),
            long: ko.observable(""),
            exists: ko.pureComputed(() => latLngExists(model.lastCheckIn.lat()) || model.lastCheckIn.long())
        }
    },
    latLngExists = val => val || val === 0 || val === "0",
    $ajax$checkIn$always = () => model.checkingIn(false),
    $ajax$checkIn$success = location => {
        toastSuccess('Checked in successfully!');
        $ajax$done$retrieveLatestCheckin({location});
    },
    $ajax$done$retrieveLatestCheckin = r => {
        if (r && r.location) {
            model.lastCheckIn.lat(r.location.latitude);
            model.lastCheckIn.long(r.location.longitude);
        } else {
            model.lastCheckIn.lat("");
            model.lastCheckIn.long("");
        }
    },
    $ajax$fail$retrieveLatestCheckin = () => $ajax$done$retrieveLatestCheckin(null),
    retrieveLatestCheckin = () => {
        $.getJSON(`${PATH}api/user`)
            .fail($ajax$fail$retrieveLatestCheckin)
            .done($ajax$done$retrieveLatestCheckin)
    };

export const MAX_LAT = 86.0;
export const MAX_LNG = 181;

export const coordinateIsValid = (value, max) => {
    try {
        value = parseFloat(value);
        if (value === null || value === "" || isNaN(value) || value > max || value < -max) {
            return false;
        }
        return true;
    } catch (e) {
        return false;
    }
};

glob.loggedIn.subscribe(loggedIn => {
    if (loggedIn) {
        retrieveLatestCheckin();
    } else {
        model.lastCheckIn.lat("");
        model.lastCheckIn.long("");
    }
});

if (glob.loggedIn()) {
    retrieveLatestCheckin();
}


$("#checkin-form").submit(e => {
    e.preventDefault();
    const params = {
        lat: model.checkInLatitude(),
        long: model.checkInLongitude()
    };

    model.checkingIn(true);
    $.ajax(`${PATH}api/user/check-in?${qs(params)}`, {
        method: 'PUT'
    }).always($ajax$checkIn$always)
        .fail(toastErrorHandler)
        .done($ajax$checkIn$success);
});

export default model;