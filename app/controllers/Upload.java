package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import utils.S3Utils;

import javax.inject.Inject;
import java.io.File;

public class Upload extends Controller {

    private final S3Utils s3Utils;

    @Inject
    public Upload(S3Utils s3Utils) {
        this.s3Utils = s3Utils;
    }

    /**
     * @return 200 (ok)
     */
    public Result upload(File file) {
        this.s3Utils.uploadFileAndReturnPublicUrl("mybucket", "test", file, 1);
        return ok();
    }
}