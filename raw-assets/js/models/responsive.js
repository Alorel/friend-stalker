import $ from "jquery";
import ko from "knockout";
import listen from "../util/passive-event-listener-with-fallback.js";

/*
 Responsiveness models; no longer used, but kept anyway as it won't get compiled into the main file
 unless any other modules reference it.
 */

const $parent = $('<div id="responsive-monitor"/>'),
    classes = ['xs', 'sm', 'md', 'lg'];

$parent.appendTo(document.body);

let model = {},
    elements = {};

for (let clazz of classes) {
    elements[`responsive_${clazz}`] = $(`<span/>`).addClass(`hidden-${clazz}`);
    elements[`responsive_${clazz}`].appendTo($parent);
    model[`responsive_${clazz}_visible`] = ko.observable(elements[`responsive_${clazz}`].is(":visible"))
        .extend({rateLimit: 250});
}

model.responsive_class = ko.pureComputed(() => {
    if (!model.responsive_lg_visible()) {
        return 'lg';
    } else if (!model.responsive_md_visible()) {
        return 'md';
    } else if (!model.responsive_sm_visible()) {
        return 'sm';
    } else if (!model.responsive_xs_visible()) {
        return 'xs';
    }

    return '';
});

const listener = () => {
    model.responsive_lg_visible(elements.responsive_lg.is(":visible"));
    model.responsive_md_visible(elements.responsive_md.is(":visible"));
    model.responsive_sm_visible(elements.responsive_sm.is(":visible"));
    model.responsive_xs_visible(elements.responsive_xs.is(":visible"));
};

listen(window, 'resize', listener);

export  default model;