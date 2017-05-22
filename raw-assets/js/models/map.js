import map from "./map/google-map.loader.js";
import MyPosition from "./map/my-position.js";
import PositionManager from "./map/position-manager.js";
import checkInModel from "./check-in.js";
import mFriends from "./friends-list.js";

/*
 Map model
 */

let friendMarkers = {};

export const clearFriendMarkers = () => {
    for (let friendID of Object.keys(friendMarkers)) {
        friendMarkers[friendID].removeMarker();
    }
    friendMarkers = {};
};

export const getFriendMarker = friendID => friendMarkers[friendID] || null;

export default map.then(map => {
    map.addListener('click', e => {
        checkInModel.checkInLatitude(e.latLng.lat());
        checkInModel.checkInLongitude(e.latLng.lng());
    });
    const userPosition = new MyPosition(map);
    userPosition.panTo().centerOn();

    const friendListener = friends => {
        clearFriendMarkers();
        for (let spec of friends) {
            if (spec.location) {
                friendMarkers[spec.id] = new PositionManager(map, spec.id);
                friendMarkers[spec.id].createMarker(
                    spec.location.latitude,
                    spec.location.longitude
                );
            }
        }
    };

    mFriends.friends.subscribe(friendListener);
    friendListener(mFriends.friends());

    return map;
});