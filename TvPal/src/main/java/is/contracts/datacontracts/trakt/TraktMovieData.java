package is.contracts.datacontracts.trakt;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arnar on 21.11.2013.
 */
public class TraktMovieData
{
    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("images")
    private TraktImage image;

    @SerializedName("imdb_id")
    private String imdbId;

    @SerializedName("genres")
    private List<String> genres;

    public String getTitle() { return this.title; }
    public String getOverview() { return this.overview; }
    public TraktImage getImage() { return this.image; }
    public String getImdbId() { return this.imdbId; }
    public List<String> getGenres() { return genres;}

    public void setTitle(String title) { this.title = title;}
    public void setOverview(String overview) { this.overview = overview;}
    public void setImage(TraktImage image) { this.image = image; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId;};
}