package utils;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Singleton
public class S3Utils {

    private static final Logger logger = LoggerFactory.getLogger(S3Utils.class);

    private final Config configuration;
    private final Environment environment;
    private final AmazonS3 s3client;

    @Inject
    public S3Utils(Config configuration,
                   Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
        this.s3client = createClient();
    }

    private AmazonS3Client createClient() {
        AmazonS3 s3;

        if (environment.isDev()) {
            AWSCredentials credentials = new BasicAWSCredentials(configuration.getString("aws.accessKey"), configuration.getString("aws.secretKey"));
            AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(credentialsProvider).build();
        } else {
            s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
        }

        return (AmazonS3Client) s3;
    }

    public AmazonS3 getS3client() {
        return s3client;
    }

    /**
     * Read a file from S3 and return his content.
     *
     * @param bucketName : the name of the bucket
     * @param fileName   : the path of the file (ex: path/to/my/file)
     * @return
     * @throws FileNotFoundException : if the bucket or the file does not exist
     */
    public String readFile(String bucketName, String fileName) throws FileNotFoundException {
        if (s3client.doesObjectExist(bucketName, fileName)) {
            return s3client.getObjectAsString(bucketName, fileName);
        }
        throw new FileNotFoundException("the file " + fileName + " in the bucket " + bucketName + " does not exists");
    }

    public void uploadFile(String bucketName, String key, File file) {
        this.s3client.putObject(bucketName, key, file);
    }

    public Optional<String> uploadFileAndReturnPublicUrl(String bucketName, String key, File file, long expirationDays) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file).withCannedAcl(CannedAccessControlList.PublicRead);
            this.s3client.putObject(putObjectRequest);

            Date expirationDate = new Date();

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expirationDate);

            URL url = this.s3client.generatePresignedUrl(generatePresignedUrlRequest);

            return Optional.of(url.toString());
        } catch (Exception e) {
            logger.warn("uploadFileAndReturnPublicUrl failed", e);
            return Optional.empty();
        }
    }

    public void createBucketIfNotExist(String bucketName) {
        if (!this.s3client.doesBucketExist(bucketName)) {
            s3client.createBucket(bucketName);
            logger.warn("bucket {} doesn't exist and creates for it", bucketName);
        }
    }

    public void deleteFileByKey(String bucketName, String key) {
        this.s3client.deleteObject(bucketName, key);
    }

    public List<S3ObjectSummary> getObjectSummaries(String bucketName, String prefix) {
        return this.s3client.listObjects(bucketName, prefix).getObjectSummaries();
    }

    public void downloadFile(String bucketName, String key, String destFile) {
        this.s3client.getObject(new GetObjectRequest(bucketName, key), new File(destFile));
    }
}