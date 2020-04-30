package julia.books.error;

import lombok.Value;

@Value
class ErrorRepresentation {
    String message;

    public ErrorRepresentation(String message) {
        this.message = message;
    }
}
