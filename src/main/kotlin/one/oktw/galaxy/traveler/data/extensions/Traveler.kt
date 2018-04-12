package one.oktw.galaxy.traveler.data.extensions

import one.oktw.galaxy.Main
import one.oktw.galaxy.traveler.data.Traveler

fun Traveler.save() {
    Main.travelerManager.saveTraveler(this)
}