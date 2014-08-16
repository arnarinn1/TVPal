package is.datacontracts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventData implements Serializable
{
    @SerializedName("title")       private String title;
    @SerializedName("start_time") private String startTime;
    @SerializedName("duration")    private String duration;
    @SerializedName("description")  private String description;
    @SerializedName("event_date")  private String eventDate;

    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getDuration() { return this.duration; }
    public String getStartTime() { return this.startTime; }
    public String getEventDate() { return this.eventDate; }

    @Override
    public String toString()
    {
        return String.format("%s - %s", this.startTime, this.title);
    }
}
