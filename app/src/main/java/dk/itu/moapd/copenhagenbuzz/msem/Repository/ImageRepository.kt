package dk.itu.moapd.copenhagenbuzz.msem.Repository

import dk.itu.moapd.copenhagenbuzz.msem.storage
import kotlinx.coroutines.tasks.await

class ImageRepository : IRepository<ByteArray> {
    override suspend fun upload(key: String, data: ByteArray): String {
        val imageName = "${System.currentTimeMillis()}.jpg"
        val ref = storage.child(key).child(imageName)
        ref.putBytes(data).await()
        return ref.downloadUrl.await().toString()
    }
    override fun get(key: String): ByteArray {
        TODO("Not yet implemented")
    }
    override fun update(key: String, newItem: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
    override fun delete(key: String): ByteArray {
        TODO("Not yet implemented")
    }


}