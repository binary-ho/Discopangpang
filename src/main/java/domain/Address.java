package domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;

    protected Address() {
    }

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }
}
