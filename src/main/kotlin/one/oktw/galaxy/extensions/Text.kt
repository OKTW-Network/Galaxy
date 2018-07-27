package one.oktw.galaxy.extensions

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

fun String.deserialize(): Text = TextSerializers.FORMATTING_CODE.deserialize(this)

fun Text.serialize(): String = TextSerializers.FORMATTING_CODE.serialize(this)
