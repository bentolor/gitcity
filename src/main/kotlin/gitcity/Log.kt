package gitcity

/**
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */

var TRACE = true
var DEBUG = true

fun trace(msg: Any?) {
    if (TRACE) println("[TRACE] $msg")
}

fun debug(msg: Any?) {
    if (DEBUG) println("[DEBUG] $msg")
}

fun info(msg: Any?) {
    println("[ INFO] $msg")
}

fun warn(msg: Any?) {
    println("[ WARN] $msg")
}