package ec.edu.udla.fitsyncpro.models;

public class VideoTutorial {
    private String idVideo;
    private String idEjercicioAsociado;
    private String descripcion;
    private String urlYoutube;

    public VideoTutorial(String idVideo, String idEjercicioAsociado, String descripcion, String urlYoutube) {
        this.idVideo = idVideo;
        this.idEjercicioAsociado = idEjercicioAsociado;
        this.descripcion = descripcion;
        this.urlYoutube = urlYoutube;
    }

    public String getIdVideo() { return idVideo; }
    public String getIdEjercicioAsociado() { return idEjercicioAsociado; }
    public String getDescripcion() { return descripcion; }
    public String getUrlYoutube() { return urlYoutube; }

    @Override
    public String toString() {
        return descripcion + " → " + urlYoutube;
    }
}
