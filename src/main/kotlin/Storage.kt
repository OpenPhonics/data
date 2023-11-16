package com.openphonics.data

import com.google.cloud.storage.*
import com.google.cloud.storage.Storage
import java.nio.file.Files
import java.nio.file.Paths

class Storage {
    private val storage: Storage = StorageOptions.getDefaultInstance().service
    fun create(bucketName: String): Bucket {
        return storage.create(BucketInfo.of(bucketName))
    }
    fun info(bucketName: String): Iterable<Blob> {
        val bucket = storage.get(bucketName) ?: error("Bucket $bucketName does not exist")
        return bucket.list().iterateAll()
    }
    fun upload(path: String, bucketName: String, blobName: String): Blob {
        val file = Paths.get(path)
        val bucket = storage.get(bucketName) ?: error("Bucket $bucketName does not exist")
        return bucket.create(blobName, Files.readAllBytes(file))
    }
    fun delete(bucketName: String, blobName: String) {
        val bucket = storage.get(bucketName) ?: error("Bucket $bucketName does not exist")
        val blob = bucket.get(blobName) ?: error("Blob $blobName does not exist")
        blob.delete()
    }
    fun delete(bucketName: String){
        val bucket = storage.get(bucketName) ?: error("Bucket $bucketName does not exist")
        for (blob in bucket.list().iterateAll()) {
            blob.delete()
        }
        bucket.delete()
    }
}