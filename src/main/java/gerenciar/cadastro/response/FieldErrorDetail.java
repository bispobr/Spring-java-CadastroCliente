package gerenciar.cadastro.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)

@Getter
public class FieldErrorDetail {


    private final String field;
    private final String message;
    private final Object rejectedValue;

    public FieldErrorDetail(String field, String message, Object rejectedValue) {
        this.field         = field;
        this.message       = message;
        this.rejectedValue = rejectedValue;
    }


}