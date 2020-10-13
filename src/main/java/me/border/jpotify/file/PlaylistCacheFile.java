package me.border.jpotify.file;

import me.border.utilities.file.AbstractSerializedFile;

import java.io.File;

public class PlaylistCacheFile extends AbstractSerializedFile<String> {
    public PlaylistCacheFile(String file, File path, String item) {
        super(file, path, item);
    }
}
