import {resolveMessage} from "./xhr-toast-error-handler.js";

/*
 Outputs a xhr error via the given observable and sets its CSS class
 */

export default (messageObservable, classObservable, error, xhr) => {
    messageObservable(resolveMessage(xhr, null, error));
    classObservable("text-danger");
};