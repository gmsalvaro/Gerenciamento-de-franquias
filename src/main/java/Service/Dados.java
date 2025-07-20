package Service;

import Model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dados {
    private ObjectMapper objectMapper;

    public  Dados() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // config json
    }

    <T> List<T> carregarDados(String filePath, TypeReference<List<T>> typeRef) {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(filePath), "[]".getBytes());
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo JSON vazio: " + filePath + " - " + e.getMessage());
            }
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, typeRef);
        } catch (MismatchedInputException e) {
            System.err.println("Erro de desserialização (formato JSON inválido ou tipo incompatível) em " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    List<Usuario> carregarUsuarios(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(filePath), "[]".getBytes());
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo JSON vazio para usuários: " + filePath + " - " + e.getMessage());
            }
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Usuario>>() {});
        } catch (MismatchedInputException e) {
            System.err.println("Erro de desserialização (formato JSON inválido ou tipo incompatível) em " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de usuários de " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    <T> void salvarDados(String filePath, List<T> data) {
        try {
            objectMapper.writeValue(new File(filePath), data);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados em " + filePath + ": " + e.getMessage());
        }
    }
}
