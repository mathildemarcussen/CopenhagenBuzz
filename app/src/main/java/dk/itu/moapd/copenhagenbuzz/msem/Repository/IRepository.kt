package dk.itu.moapd.copenhagenbuzz.msem.Repository

interface IRepository<T> {
    suspend fun upload(key: String, data: T): String
    fun get(key: String): T
    fun update(key: String, newItem: T): T
    fun delete(key: String): T
}