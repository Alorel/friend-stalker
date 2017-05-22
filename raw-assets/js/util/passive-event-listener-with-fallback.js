import $ from "jquery";

/*
 Compatibility layer for adding passive event listeners as jQuery doesn't support those. Falls back to regular
 jQuery if passive events aren't supported by the browser.
 */

export const add = (target, evt, listener) => {
    try {
        target.addEventListener(evt, listener, {passive: true});
    } catch (e) {
        $(target).on(evt, listener);
    }
};
export const remove = (target, evt, listener) => {
    try {
        target.removeEventListener(evt, listener, {passive: true});
    } catch (e) {
        $(target).off(evt, listener);
    }
};
export default add;