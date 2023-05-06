package Model;

import java.util.List;
import java.util.Map;

public class Kullanici {

    private String id;
    private String kullaniciadi;
    private String ad;
    private String resimurl;
    private String bio;



    private String girisYili;

    private String mezunYili;

    private String telNo;

    private String mail;

    private String isBilgileri;

    private Map<String,Boolean> EgitimDurumu;

    private String ulke;
    private String sehir;
    private String sirket;

    public Kullanici(){

    }

    public Kullanici(String id, String kullaniciadi, String ad, String resimurl, String bio, String girisYili, String mezunYili, String mail, String isBilgileri, String telNo, Map <String,
            Boolean> EgitimDurumuç, String ulke, String sehir, String sirket) {
        this.id = id;
        this.kullaniciadi = kullaniciadi;
        this.ad = ad;
        this.resimurl = resimurl;
        this.bio = bio;
        this.girisYili = girisYili;
        this.mezunYili = mezunYili;
        this.isBilgileri = isBilgileri;


        this.mail = mail;
        this.telNo = telNo;
        this.EgitimDurumu = EgitimDurumu;

        this.ulke = ulke;
        this.sehir = sehir;
        this.sirket = sirket;

    }

    public String getUlke() {
        return ulke;
    }

    public void setUlke(String ulke) {
        this.ulke = ulke;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getSirket() {
        return sirket;
    }

    public void setSirket(String sirket) {
        this.sirket = sirket;
    }

    public Map<String, Boolean> getEgitimDurumu() {
        return EgitimDurumu;
    }

    public void setEgitimDurumu(Map<String, Boolean> egitimDurumu) {
        EgitimDurumu = egitimDurumu;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIsBilgileri() {
        return isBilgileri;
    }

    public void setIsBilgileri(String isBilgileri) {
        this.isBilgileri = isBilgileri;
    }

    public String getGirisYili() {
        return girisYili;
    }

    public void setGirisYili(String girisYili) {
        this.girisYili = girisYili;
    }

    public String getMezunYili() {
        return mezunYili;
    }

    public void setMezunYili(String mezunYili) {
        this.mezunYili = mezunYili;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getResimurl() {
        return resimurl;
    }

    public void setResimurl(String resimurl) {
        this.resimurl = resimurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
