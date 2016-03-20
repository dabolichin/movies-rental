package org.dabolichin.mrental.customers.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import javaslang.control.Option;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,
        getterVisibility= JsonAutoDetect.Visibility.NONE,
        isGetterVisibility= JsonAutoDetect.Visibility.NONE,
        setterVisibility= JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResource {

    public Option<String> id;

    public String firstName;

    public String lastName;

    public String email;

    public String phone;

    public CustomerResource() {}

    public CustomerResource(
            Option<String> id,
            String firstName,
            String lastName,
            String email,
            String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
}
