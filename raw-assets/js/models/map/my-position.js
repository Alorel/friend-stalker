import checkInModel, {coordinateIsValid, MAX_LAT, MAX_LNG} from "../check-in.js";
import ko from "knockout";
import PositionManager from "./position-manager.js";
import PATH from "path";

const myLatLng = ko.pureComputed(() => {
    const lat = checkInModel.lastCheckIn.lat(),
        lng = checkInModel.lastCheckIn.long();

    if (coordinateIsValid(lat, MAX_LAT) && coordinateIsValid(lng, MAX_LNG)) {
        return {lat, lng};
    }
    return null;
});

const createPositionListener = manager => {
    const listener = val => {
        if (val) {
            if (!manager.markerIsSet) {
                manager.createMarker();
            }
            manager.setCoordinates(val.lat, val.lng);
        } else {
            manager.removeMarker();
        }
    };

    myLatLng.subscribe(listener);
    listener(myLatLng());

    return listener;
};

/**
 * Manages the logged in user's position on the map
 * @author a.molcanovas@gmail.com
 */
class MyPosition extends PositionManager {

    constructor(map) {
        super(map, "You", {
            icon: `${PATH}static/you.svg`
        });
        createPositionListener(this);
    }

    createMarker() {
        try {
            const ll = myLatLng();
            if (ll !== null) {
                return super.createMarker(ll.lat, ll.lng);
            }
        } catch (e) {
            console.error(e);
        }

        return false;
    }
}

export default MyPosition;