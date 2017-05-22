/**
 * Format object into a queryString
 * @param params The object
 * @returns {string} A constructed queryString
 */
export default params => {
    let out = [];
    for (let k of Object.keys(params)) {
        out.push(`${k}=${encodeURIComponent(params[k])}`);
    }
    return out.join("&");
};