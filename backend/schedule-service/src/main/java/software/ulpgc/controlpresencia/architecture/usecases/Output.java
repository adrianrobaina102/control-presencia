package software.ulpgc.controlpresencia.architecture.usecases;

public record Output<T> (int status, T result, String error) {
    public static <T> Output<T> ok(T result) {
        return new Output<>(200, result, null);
    }

    public static <T> Output<T> created(T result) {
        return new Output<>(201, result, null);
    }

    public static <T> Output<T> badRequest(String error) {
        return new Output<>(400, null, error);
    }

    public static <T> Output<T> notFound(String error) {
        return new Output<>(404, null, error);
    }

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
}
