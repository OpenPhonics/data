package com.openphonics.data.clients

import com.google.cloud.storage.*
import com.google.cloud.storage.Bucket.BlobTargetOption
import com.google.cloud.storage.Storage
import java.nio.file.Files
import java.nio.file.Paths

class StorageClient {

    private val storage: Storage = StorageOptions.getDefaultInstance().service
    val contentType = "audio/mpeg"
    fun create(bucketName: String): String {
        return storage.get(bucketName)?.name ?: storage.create(BucketInfo.of(bucketName)).name
    }
    fun upload(path: String, bucketName: String, blobName: String): String {
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