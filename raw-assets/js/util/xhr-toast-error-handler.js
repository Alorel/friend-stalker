import {error as toast} from "./toast.js";

/*
 XHR error handler which creates toast messages on error.
 */

export const resolveMessage = (xhr, status, error) => error || xhr.responseText || status || "Unknown error";
export const xhr = (xhr, status, error) => {
    toast(resolveMessage(xhr, status, error));
};

export default xhr;