package data;

public class InButton {
    String code;
    String text;
    Runnable onClick;

    public InButton(String code, String text, Runnable onClick) {
        this.code = code;
        this.text = text;
        this.onClick = onClick;
    }

    public InButton() {
    }

    public InButton(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
