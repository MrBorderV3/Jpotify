package me.border.jpotify.file;

import me.border.utilities.file.AbstractSerializedFile;

import java.io.File;

public class CacheFile extends AbstractSerializedFile<Object[]> {
    // 0 = SONG/PLAYLIST CACHE, 1 = SOUND CACHE
    public CacheFile(String file, File path, Object[] item) {
        super(file, path, item);
    }
}
