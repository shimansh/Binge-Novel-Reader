/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mgn.bingenovelreader.util;

/**
 * Constants used by multiple classes in this package
 */
public class Constants {

    public static final String ROYAL_ROAD_SITE_THEME_ID = "siteThemeRoyalRoad";

    public static class NovelSites {
        public static final String NOVEL_UPDATES = "novelupdates.com";
        public static final String ROYAL_ROAD = "royalroadl.com";
        public static final String KOBATOCHAN = "kobatochan.com";
    }

    //region DownloadQueue Status Values
    public static final int STATUS_DOWNLOAD = 0;
    public static final int STATUS_STOPPED = 1;
    //endregion

    //region Intent Keys
    public static final String NOVEL_ID = "novelId";
    public static final String WEB_PAGE_ID = "webPageId";
    public static final String TOTAL_CHAPTERS_COUNT = "totalChaptersCount";
    public static final String CURRENT_CHAPTER_COUNT = "currentChapterCount";

    //endregion

    //region Broadcast Actions
    public static final String DOWNLOAD_QUEUE_NOVEL_UPDATE = "novelUpdate";
    public static final String DOWNLOAD_QUEUE_NOVEL_DOWNLOAD_COMPLETE = "novelDownloadComplete";
    public static final String NOVEL_DELETED = "novelDeleted";


    //endregion

    public static final String IMAGES_DIR_NAME = "images";

    //region Activity Codes
    public static final int SEARCH_REQ_CODE = 1;
    public static final int NOVEL_DETAILS_REQ_CODE = 2;
    public static final int NOVEL_DETAILS_RES_CODE = 3;
    public static final int READER_ACT_REQ_CODE = 4;
    public static final int DOWNLOAD_QUEUE_ACT_RES_CODE = 5;


    //endregion

    public static final String allowedWebsites = "2.gravatar.com " +
            "www.novelupdates.com " +
            "guqintranslations.wordpress.com " +
            "www.royalroadl.com" +
            "www.isohungrytls.com ";
}
