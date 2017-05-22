import ko from "knockout";
import $ from "jquery";

/**
 * Binding for proper HTML &lt;time&gt; tag formatting
 */
ko.bindingHandlers.longdate = {
    update: (element, valueAccessor) => {
        const val = ko.unwrap(valueAccessor()),
            isDate = val instanceof Date;

        if (isDate || !isNaN(val)) {
            let date;

            if (isDate) {
                date = val;
            } else {
                date = new Date(parseInt(val));
            }

            $(element).attr("datetime", date.toISOString()).text(date.toLocaleString());
        }
    }
};

export default ko;