package nl.hva.capstone.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class GamePlayer(
    var id: String,
    var name: String,
    var ready: Boolean,
    var owner: Boolean
) : Parcelable {

    constructor() : this("", "", false, false)
    constructor(id: String, name: String) : this(id, name, false, false)

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readBoolean(),
        parcel.readBoolean(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeBoolean(ready)
        parcel.writeBoolean(owner)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<GamePlayer> {
        override fun createFromParcel(parcel: Parcel) = GamePlayer(parcel)
        override fun newArray(size: Int) = arrayOfNulls<GamePlayer>(size)
    }
}