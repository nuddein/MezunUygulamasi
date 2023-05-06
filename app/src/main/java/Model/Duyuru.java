package Model;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Duyuru {
    private String id;
    private String title;
    private String content;
    private long deadline;
    private String deadlineString;
    private String userId;

    public Duyuru() {
    }

    public Duyuru(String id, String title, String content, long deadline, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.userId = userId;

        // Deadline'ı formatlayın ve deadlineString'e atayın
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        this.deadlineString = sdf.format(deadline);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDuyuruId() {
        return id;
    }

    public void setDuyuruId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;

        // Yeni deadline değeri için deadlineString'i güncelleyin
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        this.deadlineString = sdf.format(deadline);
    }

    public String getDeadlineString() {
        return deadlineString;
    }

    public void setDeadlineString(String deadlineString) {
        this.deadlineString = deadlineString;
    }
}
