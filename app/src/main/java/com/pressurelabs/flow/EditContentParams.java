package com.pressurelabs.flow;

import com.google.android.gms.drive.DriveFile;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-09-08
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class EditContentParams {
    public DriveFile getFileToWrite() {
        return fileToWrite;
    }

    public String getDataToWrite() {
        return dataToWrite;
    }

    DriveFile fileToWrite;
    String dataToWrite;

    public EditContentParams(String dataToWrite, DriveFile fileToWrite) {
        this.dataToWrite = dataToWrite;
        this.fileToWrite = fileToWrite;
    }
}
