package br.com.seuprojeto.app_vendas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.seuprojeto.app_vendas.dto.ErroRespostaDTO;

// @RestControllerAdvice: Anotação que transforma esta classe no nosso "Plantão de Emergência" global.
@RestControllerAdvice
public class ManipuladorDeExcecoes {

    // @ExceptionHandler: Diz que este método será acionado sempre que uma exceção do tipo
    // RuntimeException (ou qualquer classe filha dela) for lançada por algum controller.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRuntimeException(RuntimeException ex) {
        // Criamos nosso objeto de erro padronizado.
        // Pegamos a mensagem da exceção (ex: "Este e-mail já está em uso.")
        // e o código de status HTTP 400.
        ErroRespostaDTO erro = new ErroRespostaDTO(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        
        // Retornamos uma resposta HTTP com o status 400 e o nosso objeto de erro como corpo (body).
        // O Spring se encarregará de transformar o objeto 'erro' em JSON.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}