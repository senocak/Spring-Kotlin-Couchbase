package com.github.senocak.config

import com.couchbase.client.core.error.BucketNotFoundException
import com.couchbase.client.java.Bucket
import com.couchbase.client.java.Cluster
import com.couchbase.client.java.env.ClusterEnvironment
import com.github.senocak.util.logger
import org.slf4j.Logger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories
import org.springframework.stereotype.Component

@Configuration
@EnableCouchbaseRepositories
class CouchbaseConfiguration(
    private val dataSourceConfig: DataSourceConfig
): AbstractCouchbaseConfiguration() {
    private val log: Logger by logger()

    override fun getConnectionString(): String = dataSourceConfig.url
    override fun getUserName(): String = dataSourceConfig.username
    override fun getPassword(): String = dataSourceConfig.password
    override fun getBucketName(): String = dataSourceConfig.bucketName
    override fun typeKey(): String = "type"
    override fun getScopeName(): String = dataSourceConfig.scope

    @Bean(destroyMethod = "disconnect")
    override fun couchbaseCluster(couchbaseClusterEnvironment: ClusterEnvironment): Cluster {
        try {
            log.info("Connecting to Couchbase cluster at ")
            return Cluster.connect(connectionString, userName, getPassword())
        } catch (e: Exception) {
            log.error("Error connecting to Couchbase cluster: ${e.message}")
            throw e
        }
    }

    @Bean
    fun getCouchbaseBucket(cluster: Cluster): Bucket =
        try {
            if (!cluster.buckets().allBuckets.containsKey(getBucketName())) {
                log.error("Bucket with name ${getBucketName()} does not exist. Creating it now.")
                throw BucketNotFoundException(bucketName)
            }
            cluster.bucket(getBucketName())
        } catch (e: Exception) {
            log.error("Error getting bucket, ${e.message}")
            throw e
        }
}

@Component
@ConfigurationProperties(prefix = "spring.datasource")
class DataSourceConfig {
    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
    lateinit var bucketName: String
    lateinit var scope: String
    lateinit var ddl: String
}
