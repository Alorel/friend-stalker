import ko from "knockout";
import $ from "jquery";

/**
 * Binding to slide-hide/remove an element
 */

ko.bindingHandlers.slideVisible = {
    init: (element, valueAccessor) => {
        if (ko.unwrap(valueAccessor())) {
            $(element).show();
        } else {
            $(element).hide();
        }
    },
    update: (element, valueAccessor) => {
        if (ko.unwrap(valueAccessor())) {
            $(element).slideDown();
        } else {
            $(element).slideUp();
        }
    }
};

export default ko;