package tn.dymes.store.dtos;

import java.util.List;

public record AddEmployeeDTO(
        String gender, String firstName, String lastName, String email,
        String password, String phoneNumber, String BIO, List<ModuleDTO> modules
) {
}
