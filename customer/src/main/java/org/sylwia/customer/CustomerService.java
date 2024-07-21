package org.sylwia.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sylwia.clients.fraud.FraudCheckHistoryResponse;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        checkIfEmailValid(email);
        checkIfEmailTaken(email);

        Customer customer = Customer.builder()
                .firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .email(customerRegistrationRequest.email())
                .build();

        customerRepository.saveAndFlush(customer);

        final RestTemplate restTemplate = new RestTemplate();
        FraudCheckHistoryResponse fraudCheckHistoryResponse = restTemplate.getForObject(
                "http://localhost:8081/api/v1/fraud-check/{customerId}",
                FraudCheckHistoryResponse.class,
                customer.getId()
        );
        assert fraudCheckHistoryResponse != null;
        if (fraudCheckHistoryResponse.fraudster()) {
            throw new IllegalStateException("fraudster");
        }
    }

    private void checkIfEmailTaken(String email) {
        Optional<Customer> optionalStudent = customerRepository.findCustomerByEmail(email);
        if (optionalStudent.isPresent()) {
            throw new IllegalStateException("Email taken");
        }
    }

    private void checkIfEmailValid(String email) {
        String regex = "^(.+)@(.+)$";

        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(email).matches()) {
            throw new IllegalStateException("Email not valid");
        }
    }
}
