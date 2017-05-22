import ko from "knockout";
import $ from "jquery";

/**
 * Binding to control an element's visibility CSS attribute
 */
ko.bindingHandlers.visibility = {
    update: (element, valueAccessor) => {
        $(element).css("visibility", ko.unwrap(valueAccessor()) ? "visible" : "hidden");
    }
};

export default ko;