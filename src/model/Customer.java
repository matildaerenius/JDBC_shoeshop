package model;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String city;
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }


    public Customer(int id, String firstName, String lastName, String city, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.email = email;
        this.password = password;
    }

    // En till konstruktor utan id för registrering av ny kund
    public Customer(String firstName, String lastName, String city, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.email = email;
        this.password = password;
    }


}
