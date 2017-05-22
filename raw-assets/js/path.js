/*
 This isn't required in ANY other programming language, but Java just HAS to be a nuisance with its context path nonsense.
 Makes sure that the context path is taken into account when calculating paths for AJAX requests.
 */

export default location.pathname.replace(/\/$/, '') + "/";