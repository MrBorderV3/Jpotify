package me.border.jpotify.audio.util;

import me.border.jpotify.audio.Song;

import java.util.LinkedList;

public class SongQueue extends LinkedList<Song> {

    /**
     * Retrieves the head of the history (last element added) and removes
     *
     * @return The head of the history or null if empty
     */
    public Song poll(){
        if (isEmpty()){
            return null;
        }

        return removeLast();
    }
}
