import $ from "jquery";
import css from "lib/toast/jquery.toast.min.css!text";
import "../jquery-fn/slide-and-remove.js";
import "jquery-toast";

/*
 Toast message library
 */

$('<style/>').text(css).appendTo(document.body);

$(document).on('click', '.jq-toast-wrap>div', function () {
    $(this).slideAndRemove();
});

const defaultOpts = {
        showHideTransition: 'fade', // fade, slide or plain
        allowToastClose: true, // Boolean value true or false
        hideAfter: false, // false to make it sticky or number representing the miliseconds as time after which toast needs to be hidden
        stack: 10, // false if there should be only one toast at a time or a number representing the maximum number of toasts to be shown at a time
        position: 'top-right', // bottom-left or bottom-right or bottom-center or top-left or top-right or top-center or mid-center or an object representing the left, right, top, bottom values
        textAlign: 'left',  // Text alignment i.e. left, right or center
        loader: false,  // Whether to show loader or not. True by default
        loaderBg: '#9EC600'  // Background color of the toast loader
    },
    levels = ['info', 'success', 'warning', 'error'],
    out = {},
    doToast = (message, icon, options) => {
        return $.toast($.extend({
            text: message,
            icon: icon
        }, defaultOpts, options || {}));
    };

for (let level of levels) {
    out[level] = (message, options) => doToast(message, level, options);
}

export default out;
export const info = out.info;
export const success = out.success;
export const warning = out.warning;
export const error = out.error;