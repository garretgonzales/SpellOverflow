package dto;


// record: immutable data carrier
public record RegisterUserResponse(

        Long id,
        String username,
        String email

) {

}
