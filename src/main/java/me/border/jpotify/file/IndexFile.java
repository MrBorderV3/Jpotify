package me.border.jpotify.file;

import me.border.jpotify.audio.Playlist;
import me.border.utilities.file.AbstractSerializedFile;

import java.io.File;
import java.util.Map;

public class IndexFile extends AbstractSerializedFile<Map<File, String>> {

    public IndexFile(Playlist playlist, Map<File, String> item) {
        super("indices", playlist.getDir(), item);
    }


}
