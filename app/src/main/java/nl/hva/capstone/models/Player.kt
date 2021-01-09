package nl.hva.capstone.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(
    var id: String,
    var name: String,
) : Parcelable {

    constructor() : this("", "")

    constructor(name: String) : this("", name)

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}
