package ec.edu.udla.fitsyncpro.models;

/**
 * VIDEOTUTORIAL: enlace de YouTube asociado a un ejercicio. Permite que el
 * socio vea la técnica correcta. Se relaciona con Ejercicio por el id guardado
 * en idEjercicioAsociado (clave del HashMap de videos en GestorRutinas).
 */
public class VideoTutorial {
    private String idVideo;
    private String idEjercicioAsociado;  // id del ejercicio al que pertenece el video
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
