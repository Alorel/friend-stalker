import ko from "knockout-lib";

/*
 Setup for Knockout. other modules depend on THIS module instead of the actual Knockout, ensuring that all the
 statements defined below are applied before the library is used.
 */

ko.options.deferUpdates = true;
export default ko;