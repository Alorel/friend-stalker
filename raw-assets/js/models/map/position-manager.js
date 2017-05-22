import {coordinateIsValid, MAX_LAT, MAX_LNG} from "../check-in.js";
import $ from "jquery";
import PATH from "path";

const defaultMarkerOptions = {
    icon: `${PATH}static/them.svg`
};

const labelOptions = {
    fontWeight: 'bold'
};

/**
 * Manages a user's position on the map
 *
 * @author a.molcanovas@gmail.com
 */
class PositionManager {
    constructor(map, label = null, markerOptions = {}) {
        if (!map) {
            throw new Error("Map not set!");
        }
        /** @private */
        this._map = map;

        /** @private */
        this._marker = null;

        /** @private */
        this._label = label;

        /** @private */
        this._markerOptions = markerOptions;
    }

    set markerOptions(value) {
        this._markerOptions = value;
    }

    get label() {
        return this._label;
    }

    get markerOptions() {
        return this._markerOptions;
    }

    set label(value) {
        this._label = value;
    }

    get map() {
        return this._map;
    }

    get markerIsSet() {
        return !!this.marker;
    }

    get marker() {
        return this._marker;
    }

    setCoordinates(lat, lng) {
        if (this.markerIsSet) {
            this.marker.setPosition({lat, lng});
            return true;
        }
        return false;
    }

    centerOn() {
        if (this.markerIsSet) {
            this.map.setCenter(this.marker.getPosition());
        }
        return this;
    }

    panTo() {
        if (this.markerIsSet) {
            this.map.panTo(this.marker.getPosition());
        }
        return this;
    }

    createMarker(lat, lng) {
        if (coordinateIsValid(lat, MAX_LAT) && coordinateIsValid(lng, MAX_LNG)) {
            if (this.markerIsSet) {
                this.marker.setMap(null);
            }
            let opts = {
                position: {lat, lng},
                map: this.map
            };

            if (this.label) {
                opts.label = $.extend({
                    text: this.label,
                    fontSize: '32px'
                }, labelOptions);
            }

            opts = $.extend(
                {},
                defaultMarkerOptions,
                opts,
                this.markerOptions
            );

            opts.icon = {
                url: opts.icon,
                labelOrigin: new google.maps.Point(16, 40)
            };

            this._marker = new google.maps.Marker(opts);

            return true;
        }

        return false;
    }

    removeMarker() {
        if (this.markerIsSet) {
            this.marker.setMap(null);
            this._marker = null;
            return true;
        }
        return false;
    }
}

export default PositionManager;