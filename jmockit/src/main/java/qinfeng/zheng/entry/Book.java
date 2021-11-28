package qinfeng.zheng.entry;

import java.util.Date;

/**
 * @Author ZhengQinfeng
 * @Date 2021/3/31 23:27
 * @dec
 */
public class Book {
    private String id;
    private String name;
    private String language;
    private String type;
    private String author;
    private String pubilisher;
    private Date publishDay;
    private String price;
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubilisher() {
        return pubilisher;
    }

    public void setPubilisher(String pubilisher) {
        this.pubilisher = pubilisher;
    }

    public Date getPublishDay() {
        return publishDay;
    }

    public void setPublishDay(Date publishDay) {
        this.publishDay = publishDay;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
