package me.border.jpotify.audio.util;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import me.border.jpotify.audio.Playlist;
import me.border.jpotify.util.ui.ConfirmBox;
import me.border.utilities.utils.AsyncScheduler;
import me.border.utilities.utils.ImmuteableResponse;
import me.border.utilities.utils.Response;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTConverter {

    private static final String API_KEY = "AIzaSyAYCPg7lQme-Xvml2zx3-FfNNX_OWACD6k";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null).setApplicationName("Jpotify").build();
    }

    public static String[] getIDByName(String name) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        YouTube.Search.List request = youtubeService.search().list("snippet");
        SearchListResponse response = request.setMaxResults(1L).setKey(API_KEY).setQ(name).setType("video").setOrder("relevance").execute();
        String[] info = new String[2];
        try {
            SearchResult item = response.getItems().get(0);
            info[0] = item.getSnippet().getTitle();
            info[1] = item.getId().getVideoId();
            return info;
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public static CompletableFuture<Response<Integer>> addSong(String search, Playlist playlist){
        CompletableFuture<Response<Integer>> future = new CompletableFuture<>();
        String id = "";
        if (!search.startsWith("https:") && !search.startsWith("http:")){
            try {
                String[] info = getIDByName(search);
                if (info == null){
                    future.complete(new ImmuteableResponse<>(false, 1));
                    return future;
                }
                String title = info[0];
                if (!ConfirmBox.showAlert("Is `" + title + "` The video you've chosen?", "Confirmation")){
                    future.complete(new ImmuteableResponse<>(false, 0));
                    return future;
                }
                id = info[1];
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            id = getYoutubeVideoId(search);
        }

        String finalId = id;
        AsyncScheduler.runTaskAsync(() -> {
            try {
                YoutubeDownloader downloader = new YoutubeDownloader();
                YoutubeVideo video = downloader.getVideo(finalId);
                video.download(video.audioFormats().get(0), playlist.getDir());
                future.complete(new ImmuteableResponse<>(true, 1));
            } catch (YoutubeException | IOException e){
                future.complete(new ImmuteableResponse<>(false, 1));
            }
        });

        return future;
    }

    private static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

}