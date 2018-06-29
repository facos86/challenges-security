package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Statistics extends Controller {
    private final StatsService service;
    /**
     *
     * @return 200 (ok)
     */
    public Result upload(File file) {
        utils.S3Utils.uploadFileAndReturnPublicUrl("mybucket", "test", File file, 1);
        return ok();
    }
}