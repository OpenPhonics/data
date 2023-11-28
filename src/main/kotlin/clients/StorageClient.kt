package com.openphonics.data.clients

import com.google.cloud.storage.*
import com.google.cloud.storage.Storage
import java.nio.file.Files
import java.nio.file.Paths

object StorageClient {

    private val storage: Storage = StorageOptions.getDefaultInstance().service
    private val contentType = "audio/mpeg"
    fun createBucket(bucketName: String) {
        storage.get(bucketName)?.name ?: storage.create(BucketInfo.of(bucketName)).name
    }
    fun uploadMp3(path: String, bucketName: String, blobName: String): String {
        val file = Paths.get(path)
        val blobId = BlobId.of(bucketName, blobName)
        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .setAcl(mutableListOf(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
            .build()
        val blob = storage.create(blobInfo, Files.readAllBytes(file))
        return blob.name
    }
}