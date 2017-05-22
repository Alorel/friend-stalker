import ko from "knockout";

/*
 Global model
 */

export default {
    username: ko.observable(""),
    localStorageSupported: 'localStorage' in window,
    loggedIn: ko.observable(false)
};