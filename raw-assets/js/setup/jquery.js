import $ from "jquery-lib";
import glob from "../model-global.js";

/*
 Setup for jQuery. other modules depend on THIS module instead of the actual jQuery, ensuring that all the statements
 defined below are applied before the library is used.
 */

$.ajaxPrefilter(options => {
    const user = glob.username();
    if (user) {
        options.headers = $.extend(options.headers || {}, {
            'x-user': glob.username()
        });
    }
});

export default $;