import $ from "jquery";

/*
 * slideAndRemove jQuery function definition
 */

function removeThis() {
    $(this).remove();
}

$.fn.slideAndRemove = function (duration, onCompletion) {
    const self = $(this);

    if (typeof duration === "function") {
        onCompletion = duration;
        duration = null;
    }

    self.slideUp(duration || 400, onCompletion ? function () {
        self.remove();
        onCompletion.bind(this)();
    } : removeThis);

    return self;
};

export default $;