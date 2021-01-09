package nl.hva.capstone.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Game(
    var id: String,
    var name: String,
    var players: ArrayList<GamePlayer>,
    var status: GameStatus,
    var board: HashMap<String, HashMap<String, String?>>,
    var turn: String,
    var winner: String
) : Parcelable {

    constructor() : this(
        "",
        "",
        arrayListOf(),
        GameStatus.PENDING,
        HashMap<String, HashMap<String, String?>>(),
        "",
        ""
    )

    constructor(name: String, players: ArrayList<GamePlayer>) : this(
        "",
        name,
        players,
        GameStatus.PENDING,
        HashMap<String, HashMap<String, String?>>(),
        "",
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(GamePlayer.CREATOR)!!,
        GameStatus.valueOf(parcel.readString()!!),
        parcel.readHashMap(null) as HashMap<String, HashMap<String, String?>>,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeTypedList(players)
        parcel.writeString(status.status)
        parcel.writeMap(board as Map<String, HashMap<String, String?>>)
        parcel.writeString(turn)
        parcel.writeString(winner)
    }

    override fun describeContents() = 0

    fun allReady(): Boolean = players.all { it.ready || it.owner }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel) = Game(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Game?>(size)
    }
}