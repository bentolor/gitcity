/*
 * Controler Util for WebVR applications.
 * by BadToxic
 */

var vrc = {};


vrc.logKeys = true; // Show key states on console
vrc.object = undefined; // Object to move on update (needs a position)
vrc.moveSpeed = 0.005; // This will be multiplied to the delta of the current frame

// Buttons
vrc.buttons = ["right", "up", "left", "down","raise","lower"]; // Tags for all available buttons
vrc.bKeyMapping = {
    39: "right", 	 // Arrow right
    87: "right",	 // W
    38: "up",  	 // Arrow up
    65: "up", 		 // A
    37: "left",		 // Arrow left
    88: "left",		 // X
    40: "down",	 // Arrow down
    68: "down", 	 // D
    73: "raise",
    75: "lower"
};
vrc.bKeyArrowsKeys = [
    39, 38, 37, 40	 // Arrow button keys
];
vrc.safariPressKeysMap = {
    119: "right",	 // W
    97: "up", 	     // A
    120: "left",	 // X
    100: "down"  // D
};
vrc.safariReleaseKeys = [
    99, 101, 113, 121, 122
];
// keycode which will stop the current moving (keyup/down)
vrc.stopKeys = [ 81, 89, 69, 67, 77, 80, 90 ];

vrc.bCheck = {};	    // Map for held down buttons
vrc.bPressed = {};	// Map for buttons pressed in this frame
vrc.bReleased = {}; // Map for buttons released in this frame
vrc.bBlocked = {};   // Block buttons for some time
vrc.blockTime = 100; // ms to ignore a pressed button after "keydown" event
vrc.keypress = "";    // Key code of last keypress event (for debugging)

// Init maps with false
vrc.buttons.forEach(function (buttonKey) {
    vrc.bCheck[buttonKey] = false;
    vrc.bPressed[buttonKey] = false;
    vrc.bReleased[buttonKey] = false;
    vrc.bBlocked[buttonKey] = 0;
});

vrc.pressButton = function (key) {
    vrc.bCheck[key] = true;
    vrc.bPressed[key] = true;
    vrc.bReleased[key] = false;
};
vrc.releaseButton = function (key) {
    vrc.bCheck[key] = false;
    vrc.bPressed[key] = false;
    vrc.bReleased[key] = true;
};
/* Call this function after every frame to reset the control flags.
 * This is not needed when using the "update" function!
 * @param delta: Seconds passed since the last frame
 * @return: Debug string with button states
 */
vrc.endFrame = function (delta) {
    var debugText = "";
    vrc.buttons.forEach(function (buttonKey) {
        vrc.bPressed[buttonKey] = false;	 // End of Frame
        vrc.bReleased[buttonKey] = false;
        debugText += buttonKey + ":  " + vrc.bCheck[buttonKey] + "<br />";
        // Blocker
        if (vrc.bBlocked[buttonKey] > 0) {
            if (vrc.logKeys)
                console.log(buttonKey + " blocked for " + vrc.bBlocked[buttonKey] + " ms (delta: " + delta + ")");
            vrc.bBlocked[buttonKey] -= delta;
        }
    });
    debugText += "keypress: " + vrc.keypress;
    vrc.keypress = "";
    return debugText;
};

/* Call this function in every frame if you have assigned an object to move.
 * "vrc.object" needs a position vector and will be moved.
 * @param delta: Seconds passed since the last frame
 * @return: Debug string with button states
 */
vrc.update = function (delta) {
    if (vrc.object !== undefined) {
        var moveDistance = delta * vrc.moveSpeed; // ? pixels per second

        if (vrc.bCheck["left"]) {
            vrc.object.translateX(-moveDistance);
            // vrc.object.position.x -= moveDistance;
        }
        if (vrc.bCheck["right"]) {
            vrc.object.translateX(moveDistance);
            // vrc.object.position.x += moveDistance;
        }
        if (vrc.bCheck["up"]) {
            vrc.object.translateZ(-moveDistance);
            // vrc.object.position.z -= moveDistance;
        }
        if (vrc.bCheck["down"]) {
            vrc.object.translateZ(moveDistance);
            // vrc.object.position.z += moveDistance;
        }
        if (vrc.bCheck["raise"]) {
            vrc.object.translateY(moveDistance);
            // vrc.object.position.z -= moveDistance;
        }
        if (vrc.bCheck["lower"]) {
            vrc.object.translateY(-moveDistance);
            // vrc.object.position.z += moveDistance;
        }
    }
    return vrc.endFrame(delta);
};

vrc.init = function () {
    document.body.addEventListener('keydown', function (e) {
        var kc = e.keyCode;
        var key = vrc.bKeyMapping[kc];
        if (vrc.logKeys) {
            console.log("keydown: e.keyCode: " + kc + " (Mapped: " + key + ")");
        }
        if (vrc.bKeyArrowsKeys.indexOf(kc) < 0) { // Skip arrow keys
            if (!vrc.bCheck[key]) { // Only when not already pressed (some browser repeat this event while hold)
                vrc.bBlocked[key] = vrc.blockTime;
            }
            if (vrc.stopKeys.indexOf(kc) > -1) {
                if (vrc.logKeys)
                    console.log("Stop key found: "+kc);

                vrc.buttons.forEach(function (buttonKey) {
                    vrc.bCheck[buttonKey] = false;
                    vrc.bPressed[buttonKey] = false;
                    vrc.bReleased[buttonKey] = false;
                    vrc.bBlocked[buttonKey] = 0;
                });
            }
        }
        vrc.pressButton(key);
    });
    document.body.addEventListener('keyup', function (e) {
        var key = vrc.bKeyMapping[e.keyCode];
        if (vrc.logKeys) {
            console.log("keyup: e.keyCode: " + e.keyCode + " (Mapped: " + key + ", Block: " + vrc.bBlocked[key] + ")");
        }
        if (vrc.bBlocked[key] <= 0) { // Don't release immediately
            vrc.releaseButton(key);
        }
    });
    document.body.addEventListener('keypress', function (e) {
        vrc.keypress = e.keyCode;
        if (vrc.logKeys) {
            console.log("keypress: e.keyCode: " + e.keyCode);
        }
        // iPhone BT-Controller special cases for Safari
        if (vrc.safariReleaseKeys.indexOf(e.keyCode) >= 0) {
            if (vrc.bCheck["right"]) {   // Release right
                vrc.releaseButton("right");
            }
            if (vrc.bCheck["up"]) {	  // Release up
                vrc.releaseButton("up");
            }
            if (vrc.bCheck["left"]) {	  // Release left
                vrc.releaseButton("left");
            }
            if (vrc.bCheck["down"]) { // Release down
                vrc.releaseButton("down");
            }
        } else {
            vrc.pressButton(vrc.safariPressKeysMap[e.keyCode]);
        }
    });
};

