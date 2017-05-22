import $ from "jquery";
import ko from "knockout";
import glob from "../model-global.js";
import outputXhrError from "../util/output-xhr-error.js";
import PATH from "path";

/*
 Model for log-in simulation
 */

const model = {
    loggingIn: ko.observable(false),
    autologinSupported: glob.localStorageSupported,
    autologin: ko.observable(glob.localStorageSupported && "0" !== localStorage.getItem("autologin")),
    logInMessage: ko.observable("Please input a username"),
    logInClass: ko.observable("text-danger"),
    loginButtonEnabled: ko.pureComputed(() => {
        return !model.loggingIn() && glob.username().length;
    })
};

model.autologin.subscribe(newValue => {
    if (model.autologinSupported) {
        localStorage.setItem("autologin", newValue ? "1" : "0");
    }
});

glob.username.subscribe(newValue => {
    if (!newValue.length) {
        model.logInClass("text-danger");
        model.logInMessage("Please input a username");
    } else {
        model.logInMessage("");
    }
});

if (model.autologinSupported) {
    // Automatically log in
    (() => {
        const savedName = localStorage.getItem("username");
        if (savedName) {
            glob.username(savedName);
            finaliseLogin();
        }
    })();
}

function $ajax$done$checkUserExists(exists) {
    if (exists) {
        finaliseLogin();
    } else {
        const user = glob.username();
        model.logInMessage(`Creating ${user}...`);
        $.post(`${PATH}api/user/${encodeURIComponent(user)}`)
            .fail($ajax$fail$loginFailed)
            .done(finaliseLogin);
    }
}

function $ajax$fail$loginFailed(xhr, status, error) {
    outputXhrError(model.logInMessage, model.logInClass, error, xhr);
    model.loggingIn(false);
}

function finaliseLogin() {
    if (model.autologin()) {
        localStorage.setItem("username", glob.username());
    }
    glob.loggedIn(true);
}

$("#login-form").submit(e => {
    e.preventDefault();
    const user = glob.username();
    model.loggingIn(true);
    model.logInClass("text-primary");
    model.logInMessage(`Checking if ${user} exists...`);
    $.getJSON(`${PATH}api/user/exists/${encodeURIComponent(user)}`)
        .done($ajax$done$checkUserExists)
        .fail($ajax$fail$loginFailed);
});

$("#log-out").click(() => {
    model.loggingIn(false);
    glob.username("");
    glob.loggedIn(false);

    if (model.autologinSupported) {
        localStorage.removeItem("username");
    }
});

export default model;