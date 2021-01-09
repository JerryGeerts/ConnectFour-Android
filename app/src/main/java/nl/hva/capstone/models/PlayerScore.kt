package nl.hva.capstone.models

class PlayerScore(
    var id: String,
    var name: String,
    var win: Int,
    var lose: Int,
) {
    constructor() : this("", "", 0, 0)
    constructor(id: String, name: String) : this(id, name, 0, 0)
}